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
const CATEGORIES = new Set(['musique', 'eau', 'terre', 'enfants', 'silent', 'restauration']);
const MARKS = new Set(['végé', 'végan', 'sans gluten', 'sans lactose', 'piquant', 'bio']);
const PROVENANCE = new Set(['confirmed', 'archived', 'unverified']);
const ts = s => (s ? new Date(s) : null);

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
  if (st && days[s.dayId]) {
    const d = days[s.dayId], dstart = ts(d.start), dend = ts(d.end);
    if (st < dstart || st >= dend)
      errors.push(`slot ${sid}: start ${s.start} outside day window ${d.id} (${d.start} -> ${d.end})`);
    if (en && en > dend)
      errors.push(`slot ${sid}: end ${s.end} runs past day window end ${d.end}`);
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

// Published opening hours must sit inside the FestivalDay window. They are different things:
// the window has to contain everything programmed that day, opening hours are what the public
// is told - beach yoga runs before the gates open.
for (const d of ed.days) {
  if (!d.opening) { warns.push(`day ${d.id}: no published opening hours`); continue; }
  if (ts(d.opening.start) < ts(d.start) || ts(d.opening.end) > ts(d.end))
    errors.push(`day ${d.id}: opening ${d.opening.start}->${d.opening.end} falls outside window ${d.start}->${d.end}`);
}
// A slot before opening is legitimate - the beach is public, so yoga runs before the gates.
// The data has to say so on purpose; an unflagged one is a mistake.
for (const s of ed.slots) {
  const d = days[s.dayId];
  if (s.start && d && d.opening && ts(s.start) < ts(d.opening.start) && s.beforeOpening !== true)
    errors.push(`slot ${s.id}: starts ${s.start}, before the site opens at ${d.opening.start} - set "beforeOpening": true if deliberate`);
  if (s.beforeOpening === true && d && d.opening && ts(s.start) >= ts(d.opening.start))
    warns.push(`slot ${s.id}: flagged beforeOpening but does not start before ${d.opening.start}`);
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

console.log(`slots=${ed.slots.length}  happenings=${Object.keys(haps).length}  days=${ed.days.length}  lanes=${ed.lanes.length}`);
console.log(`midnight-crossing slots: ${JSON.stringify(crossers)}`);
console.log(`\nERRORS (${errors.length}):`); errors.forEach(e => console.log('  x', e));
console.log(`\nWARNINGS (${warns.length}):`); warns.forEach(w => console.log('  !', w));
process.exit(errors.length ? 1 : 0);
