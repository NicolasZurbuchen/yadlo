const fs = require('fs');
const path = require('path');

const root = 'C:\\Users\\stari\\Projects\\Yadlo\\content';
const errors = [], warns = [];

function load(p) {
  try { return JSON.parse(fs.readFileSync(path.join(root, p), 'utf8')); }
  catch (e) { errors.push(`${p}: does not parse -> ${e.message}`); return null; }
}

const ed = load('editions/2026.json');
const fest = load('festival.json');
const idx = load('editions/index.json');
if (!ed || !fest || !idx) { console.log(errors.join('\n')); process.exit(1); }

const days = Object.fromEntries(ed.days.map(d => [d.id, d]));
const venues = new Set(ed.venues.map(v => v.id));
const lanes = Object.fromEntries(ed.lanes.map(l => [l.id, l]));
const sections = new Set(ed.sections.map(s => s.id));
const haps = Object.fromEntries(ed.happenings.map(h => [h.id, h]));

// The five activity kinds drive the palette. "restauration" is a sixth category for Stands only,
// which are never on the Programme and so never need a category colour beside the other five.
// The five activity kinds drive the palette. Stands use their own two, and never appear on the
// Programme, so they never need a colour beside the other five.
const CATEGORIES = new Set(['musique', 'eau', 'terre', 'enfants', 'silent', 'restauration', 'createurs']);
const MARKS = new Set(['végé', 'végan', 'sans gluten', 'sans lactose', 'piquant', 'bio']);
const PROVENANCE = new Set(['confirmed', 'archived', 'unverified']);
const ts = s => (s ? new Date(s) : null);

// An image src is either an absolute https URL, or a path relative to wherever this file was
// fetched from - exactly like a relative href in a web page. No imageBaseUrl field: the app
// already knows the address it fetched the edition from, so declaring it again in the content
// would be the same fact written twice, free to drift.
function checkSrc(src, where) {
  if (typeof src !== 'string' || !src) return errors.push(`${where}: src must be a non-empty string`);
  if (/^http:\/\//.test(src)) return errors.push(`${where}: src must be https - ${src}`);
  if (!/^https:\/\//.test(src) && src.startsWith('/'))
    errors.push(`${where}: a relative src must not start with "/" - ${src}`);
  for (const [re, what] of DIRTY_URL)
    if (re.test(src)) errors.push(`${where}: src carries a ${what}`);
}

for (const [lid, l] of Object.entries(lanes))
  if (!sections.has(l.sectionId)) errors.push(`lane ${lid}: unknown sectionId ${l.sectionId}`);

const LINK_TYPES = new Set(['spotify', 'instagram', 'website', 'soundcloud', 'bandcamp', 'facebook', 'youtube', 'tiktok', 'beatport', 'appleMusic']);
// Stored URLs must be canonical: no session ids, no viewer-locale prefixes. A link copied out of
// a browser carries whichever locale that browser was in - /intl-ja/ and /ja/ are Japanese, and
// they would follow the user into the app.
const DIRTY_URL = [
  [/[?&]si=/, '?si= session id'],
  [/[?&]srsltid=/, '?srsltid= tracking param'],
  [/[?&]hl=/, '?hl= locale param'],
  [/\/intl-[a-z]{2}\//, '/intl-xx/ locale path'],
  [/beatport\.com\/[a-z]{2}\//, 'locale path'],
];

for (const [hid, h] of Object.entries(haps)) {
  if (!CATEGORIES.has(h.category)) errors.push(`happening ${hid}: unknown category ${h.category}`);
  if (!PROVENANCE.has(h.provenance)) errors.push(`happening ${hid}: bad provenance ${h.provenance}`);
  if (!['artist', 'activity', 'stand'].includes(h.kind)) errors.push(`happening ${hid}: bad kind ${h.kind}`);
  else if (!(h.kind in h)) errors.push(`happening ${hid}: kind=${h.kind} but no '${h.kind}' payload`);
  // Content is French-only: every human-readable field is a plain string, never a {fr, en} object.
  if (typeof h.name !== 'string' || !h.name) errors.push(`happening ${hid}: name must be a non-empty string`);
  if ('description' in h && typeof h.description !== 'string')
    errors.push(`happening ${hid}: description must be a string`);

  if (!Array.isArray(h.images)) errors.push(`happening ${hid}: images must be an array`);
  else h.images.forEach((im, i) => {
    if (!im || typeof im !== 'object') return errors.push(`happening ${hid}: images[${i}] must be an object`);
    checkSrc(im.src, `happening ${hid}/images[${i}]`);
    if ('credit' in im && im.credit !== null && typeof im.credit !== 'string')
      errors.push(`happening ${hid}: images[${i}].credit must be a string or null`);
  });

  const payload = h[h.kind];
  if (payload && 'genres' in payload) {
    if (!Array.isArray(payload.genres)) errors.push(`happening ${hid}: genres must be an array`);
    else if (payload.genres.some(g => typeof g !== 'string')) errors.push(`happening ${hid}: genres must be strings`);
  }
  for (const l of (payload && payload.links) || []) {
    if (!LINK_TYPES.has(l.type)) errors.push(`happening ${hid}: unknown link type ${l.type}`);
    if (!/^https:\/\//.test(l.url)) errors.push(`happening ${hid}: link ${l.type} is not https`);
    for (const [re, what] of DIRTY_URL)
      if (re.test(l.url)) errors.push(`happening ${hid}: link ${l.type} carries a ${what} - ${l.url}`);
  }
  if (h.kind === 'artist' && (!payload.links || payload.links.length === 0))
    warns.push(`artist ${hid}: no links - needs Spotify/Instagram from the association`);

  for (const m of (payload && payload.marks) || [])
    if (!MARKS.has(m)) errors.push(`happening ${hid}: unknown stand mark "${m}"`);

  if (h.kind === 'stand') {
    const groupIds = new Set();
    for (const g of payload.menu || []) {
      if (!g.id || !g.name) errors.push(`stand ${hid}: menu group needs an id and a name`);
      if (groupIds.has(g.id)) errors.push(`stand ${hid}: duplicate menu group id ${g.id}`);
      groupIds.add(g.id);
      if (!Array.isArray(g.items) || g.items.length === 0)
        errors.push(`stand ${hid}: menu group ${g.id} has no items`);
      for (const it of g.items || []) {
        if (!it.name) errors.push(`stand ${hid}/${g.id}: item without a name`);
        if (it.price && (typeof it.price.amount !== 'number' || !it.price.currency))
          errors.push(`stand ${hid}/${g.id}/${it.name}: price needs a numeric amount and a currency`);
        if (!PROVENANCE.has(it.provenance)) errors.push(`stand ${hid}/${g.id}/${it.name}: bad provenance`);
        for (const m of it.marks || [])
          if (!MARKS.has(m)) errors.push(`stand ${hid}/${g.id}/${it.name}: unknown mark "${m}"`);
      }
    }
    if (!payload.menu || payload.menu.length === 0) warns.push(`stand ${hid}: no menu`);
  }
}

// Nothing anywhere may still be a {fr, en} object.
(function noLocalizedObjects(node, path) {
  if (Array.isArray(node)) return node.forEach((v, i) => noLocalizedObjects(v, `${path}[${i}]`));
  if (node && typeof node === 'object') {
    if ('fr' in node) errors.push(`${path}: still a localized {fr,...} object - content is French-only`);
    for (const [k, v] of Object.entries(node)) noLocalizedObjects(v, `${path}.${k}`);
  }
})(ed, '2026.json');

const seen = new Set();
for (const s of ed.slots) {
  const sid = s.id;
  if (seen.has(sid)) errors.push(`slot ${sid}: duplicate id`);
  seen.add(sid);
  if (!sid.startsWith('2026:')) errors.push(`slot ${sid}: id is not Edition-qualified`);
  if (!(s.happeningId in haps)) errors.push(`slot ${sid}: unknown happeningId ${s.happeningId}`);
  if (!(s.dayId in days)) errors.push(`slot ${sid}: unknown dayId ${s.dayId}`);
  if (!(s.laneId in lanes)) errors.push(`slot ${sid}: unknown laneId ${s.laneId}`);
  if (s.venueId !== null && !venues.has(s.venueId)) errors.push(`slot ${sid}: unknown venueId ${s.venueId}`);
  if (!PROVENANCE.has(s.provenance)) errors.push(`slot ${sid}: bad provenance ${s.provenance}`);

  const st = ts(s.start), en = ts(s.end);
  if (st && en && en <= st) errors.push(`slot ${sid}: end ${s.end} is not after start ${s.start}`);
  if (en && !st) errors.push(`slot ${sid}: has an end but no start`);
  // day.start/end are the festival's OPENING HOURS, not a bounding box. A Slot may legitimately
  // fall outside them - the beach is public, so yoga and the climbing wall run before the gates.
  // Which day a Slot belongs to is authored on the Slot, never derived from these times.
  if (st && days[s.dayId]) {
    const d = days[s.dayId];
    if (st < ts(d.start)) warns.push(`slot ${sid}: starts ${s.start.slice(11, 16)}, before the site opens at ${d.start.slice(11, 16)}`);
    if (en && en > ts(d.end)) warns.push(`slot ${sid}: ends after closing time ${d.end.slice(11, 16)}`);
  }
  if (!s.start && !s.end) warns.push(`slot ${sid}: no start and no end - cannot be placed on the Programme`);
}

// A Stand's Slots are its opening windows, and nobody has published them - so a Stand with no
// Slots is a known gap, not a broken record. Artists and Activities without one are a real fault.
for (const [hid, h] of Object.entries(haps)) {
  if (ed.slots.some(s => s.happeningId === hid)) continue;
  if (h.kind === 'stand') warns.push(`stand ${hid}: no opening hours`);
  else errors.push(`happening ${hid}: has no slots`);
}

for (const d of ed.days) {
  if (ts(d.end) <= ts(d.start)) errors.push(`day ${d.id}: closes at or before it opens`);
  if ('opening' in d) errors.push(`day ${d.id}: "opening" is gone - start/end ARE the opening hours`);
}

// One music stage: no two slots in the same lane at the same venue may overlap.
const byLaneVenue = {};
for (const s of ed.slots) {
  if (!s.start || !s.end || !s.venueId) continue;
  (byLaneVenue[`${s.laneId}@${s.venueId}`] ||= []).push(s);
}
for (const [key, group] of Object.entries(byLaneVenue)) {
  group.sort((a, b) => ts(a.start) - ts(b.start));
  for (let i = 1; i < group.length; i++)
    if (ts(group[i].start) < ts(group[i - 1].end))
      errors.push(`overlap in ${key}: ${group[i - 1].id} (ends ${group[i - 1].end}) vs ${group[i].id} (starts ${group[i].start})`);
}

// Compare the LOCAL calendar dates as written, not toISOString() - that converts to UTC and
// silently misses a 23:30->01:30 set, since +02:00 puts both ends on the same UTC day.
const crossers = ed.slots
  .filter(s => s.start && s.end && s.start.slice(0, 10) !== s.end.slice(0, 10))
  .map(s => s.id);

// Partners: unique ids, and any URL present must be canonical https.
const partnerIds = new Set();
for (const tier of ed.partners) {
  if (!PROVENANCE.has(tier.provenance)) errors.push(`partner tier ${tier.id}: bad provenance`);
  if (!tier.members || tier.members.length === 0) warns.push(`partner tier ${tier.id}: no members`);
  for (const m of tier.members || []) {
    if (partnerIds.has(m.id)) errors.push(`partner ${m.id}: duplicate id across tiers`);
    partnerIds.add(m.id);
    if (!m.name) errors.push(`partner ${m.id}: no name`);
    if (m.url === undefined) errors.push(`partner ${m.id}: url must be present, use null if unknown`);
    // A partner has a logo, not a photo: never cropped, never behind a scrim. Keeping the field
    // name distinct from "images" is what stops one being rendered as the other.
    if (m.logo === undefined) errors.push(`partner ${m.id}: logo must be present, use null if unknown`);
    else if (m.logo !== null) checkSrc(m.logo, `partner ${m.id}/logo`);
    if (m.url !== null) {
      if (!/^https:\/\//.test(m.url)) errors.push(`partner ${m.id}: url is not https - ${m.url}`);
      for (const [re, what] of DIRTY_URL)
        if (re.test(m.url)) errors.push(`partner ${m.id}: url carries a ${what}`);
    } else warns.push(`partner ${m.id}: no website found`);
  }
}

if (!idx.editions.some(e => e.id === ed.id)) errors.push(`index.json does not list edition ${ed.id}`);
for (const e of idx.editions)
  if (!fs.existsSync(path.join(root, e.path))) errors.push(`index.json: path ${e.path} does not exist`);

const emailIds = new Set(fest.contact.emails.map(e => e.id));
const refs = [
  ['accessibilite.contactEmailId', fest.accessibilite.contactEmailId],
  ['besoin.lostPropertyEmailId', fest.besoin.lostPropertyEmailId],
  ['simpliquer.hotstaff.contactEmailId', fest.simpliquer.hotstaff.contactEmailId],
  ['simpliquer.partenaire.contactEmailId', fest.simpliquer.partenaire.contactEmailId],
];
for (const [p, v] of refs) if (!emailIds.has(v)) errors.push(`festival.json ${p}: unknown email id ${v}`);
if (fest.currentEditionId !== ed.id)
  warns.push(`festival.json currentEditionId=${fest.currentEditionId} but only edition ${ed.id} authored`);

// Counted rather than listed: one line per missing image would be 36 warnings drowning the rest.
const allMembers = ed.partners.flatMap(t => t.members);
const noImage = ed.happenings.filter(h => !h.images || h.images.length === 0);
const noLogo = allMembers.filter(m => !m.logo);
if (noImage.length) warns.push(`${noImage.length}/${ed.happenings.length} happenings have no image`);
if (noLogo.length) warns.push(`${noLogo.length}/${allMembers.length} partners have no logo`);

console.log(`slots=${ed.slots.length}  happenings=${Object.keys(haps).length}  days=${ed.days.length}  lanes=${ed.lanes.length}`);
console.log(`midnight-crossing slots: ${JSON.stringify(crossers)}`);
console.log(`\nERRORS (${errors.length}):`); errors.forEach(e => console.log('  x', e));
console.log(`\nWARNINGS (${warns.length}):`); warns.forEach(w => console.log('  !', w));
process.exit(errors.length ? 1 : 0);
