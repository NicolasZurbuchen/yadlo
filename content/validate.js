const fs = require('fs');
const path = require('path');

const root = path.join(__dirname);
const errors = [], warns = [];

function load(p) {
  try { return JSON.parse(fs.readFileSync(path.join(root, p), 'utf8')); }
  catch (e) { errors.push(`${p}: does not parse -> ${e.message}`); return null; }
}

const ed = load('editions/2026/edition.json');
const fest = load('festival.json');
const idx = load('editions.json');
const ann = load('announcements.json');
if (!ed || !fest || !idx || !ann) { console.log(errors.join('\n')); process.exit(1); }

const days = Object.fromEntries(ed.days.map(d => [d.id, d]));
const haps = Object.fromEntries(ed.happenings.map(h => [h.id, h]));
// The category vocabulary is declared in the content, not hardcoded here - that is the whole
// point of keeping it: the app gets a label and an order for each filter chip without inventing
// French in Kotlin. Colour stays in the app, keyed by id, because colour is design.
const CATEGORIES = new Set(ed.categories.map(c => c.id));

// kind and category answer different questions - kind is which payload the object carries,
// category is which chip and colour it gets - but they are not independent, and nothing stops
// a typo pairing kind:"stand" with category:"musique". This is the guard rail.
//
// The lists are deliberately narrow: exactly what exists today, nothing speculative. Widening
// one should be a decision someone makes on purpose. The first realistic case is a musique
// ACTIVITY - an initiation au mix, a workshop - which is also the reason the two fields cannot
// simply be merged.
const KIND_CATEGORIES = {
  artist: ['musique'],
  activity: ['silent', 'eau', 'terre', 'enfants'],
  stand: ['restauration', 'createurs'],
};

const PROVENANCE = new Set(['confirmed', 'archived', 'unverified']);
const MARKS = new Set(['végé', 'végan', 'sans gluten', 'sans lactose', 'piquant', 'bio']);
const LINK_TYPES = new Set(['spotify', 'instagram', 'website', 'soundcloud', 'bandcamp',
  'facebook', 'youtube', 'tiktok', 'beatport', 'appleMusic']);

// Every currency and price unit in use. Narrow on purpose: "par équipe" and "par personne" are the
// only two units the festival prices anything by, and a third should be a decision rather than a
// typo that renders as "CHF 10 / equipe" on a fiche.
const CURRENCIES = new Set(['CHF']);
const PRICE_UNITS = new Set(['personne', 'équipe']);

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

const ts = s => (s ? new Date(s) : null);

function checkUrl(url, where) {
  if (!/^https:\/\//.test(url)) errors.push(`${where}: not https - ${url}`);
  for (const [re, what] of DIRTY_URL)
    if (re.test(url)) errors.push(`${where}: carries a ${what} - ${url}`);
}

// An image src is either an absolute https URL, or a path relative to wherever this file was
// fetched from - exactly like a relative href in a web page. There is no imageBaseUrl: the app
// already knows the address it fetched the edition from, so declaring it again in the content
// would be the same fact written twice, free to drift.
// Where this directory is published. The validator maps every absolute src back to a file on
// disk, which is what makes the two picture-bank checks possible: a reference to an image that
// is not there, and an image nothing references. It lives here rather than in the content
// because changing hosts should be a tooling edit, not a content migration.
const PUBLISHED_BASE = 'https://nicolaszurbuchen.github.io/yadlo/';
const referenced = new Set();

function checkSrc(src, where) {
  if (typeof src !== 'string' || !src) return errors.push(`${where}: src must be a non-empty string`);
  if (/^http:\/\//.test(src)) return errors.push(`${where}: src must be https - ${src}`);
  if (!/^https:\/\//.test(src) && src.startsWith('/'))
    errors.push(`${where}: a relative src must not start with "/" - ${src}`);
  for (const [re, what] of DIRTY_URL)
    if (re.test(src)) errors.push(`${where}: src carries a ${what}`);
  referenced.add(PUBLISHED_BASE && src.startsWith(PUBLISHED_BASE) ? src.slice(PUBLISHED_BASE.length) : src);
}

// Nothing anywhere may still be a {fr, en} object - content is French-only.
(function noLocalized(node, p) {
  if (Array.isArray(node)) return node.forEach((v, i) => noLocalized(v, `${p}[${i}]`));
  if (node && typeof node === 'object') {
    if ('fr' in node) errors.push(`${p}: still a localized {fr,...} object`);
    for (const [k, v] of Object.entries(node)) noLocalized(v, `${p}.${k}`);
  }
})(ed, '2026.json');

// --- days ---------------------------------------------------------------------------------
// start/end ARE the opening hours. Not a bounding box: a Slot may legitimately fall outside
// them, because the beach is public and yoga runs before the gates.
for (const d of ed.days) {
  if (ts(d.end) <= ts(d.start)) errors.push(`day ${d.id}: closes at or before it opens`);
  if ('opening' in d) errors.push(`day ${d.id}: "opening" is gone - start/end ARE the opening hours`);
  if (!PROVENANCE.has(d.provenance)) errors.push(`day ${d.id}: bad provenance`);
}

// --- edition-level --------------------------------------------------------------------------
// Still 1. The shapes below have changed repeatedly, but nothing has shipped and nothing reads
// these files yet, so there is no older reader to break and no version to bump away from. The
// first release is what makes this number start meaning something.
if (ed.schemaVersion !== 1) errors.push(`edition: schemaVersion must be 1, found ${ed.schemaVersion}`);
// entry and openingNote were removed: no screen consumes them yet, and content nobody renders is
// content nobody notices going stale. The FAQ still answers "is entry free?" in prose. Both come
// back as structured fields the day the Horaires and Sur place screens exist - guarded here so
// they do not drift back in unnoticed in the meantime.
if ('entry' in ed) errors.push('edition: "entry" is gone until a screen renders it - the FAQ carries the answer');
if ('openingNote' in ed) errors.push('edition: "openingNote" is gone until the Horaires screen exists');

// --- announcements ----------------------------------------------------------------------------
// Its own file because it is the only content that needs to arrive DURING the festival, when a
// correction is being pushed from a phone. Folded into festival.json it would reupload history,
// contact and transport on every annonce, and a visitor's cached copy of all of it would go stale
// together. Alone it is a few hundred bytes with its own ETag.
if (ann.schemaVersion !== 1) errors.push(`announcements: schemaVersion must be 1, found ${ann.schemaVersion}`);
if (!Array.isArray(ann.announcements)) errors.push('announcements: announcements must be an array');
else {
  const annIds = new Set();
  for (const a of ann.announcements) {
    if (annIds.has(a.id)) errors.push(`annonce ${a.id}: duplicate id`);
    annIds.add(a.id);
    if (!a.title) errors.push(`annonce ${a.id}: needs a title`);
    if (!('body' in a)) errors.push(`annonce ${a.id}: body must be present, null when the title says it all`);
    // Number.isNaN rather than a truthiness check: new Date("nonsense") is an Invalid Date, which
    // is an object, which is truthy. A bad instant would have sailed straight through.
    const at = ts(a.publishedAt);
    if (!at || Number.isNaN(at.getTime()))
      errors.push(`annonce ${a.id}: publishedAt must be an instant with an offset`);
    // Scoped to an edition, or null for something true of the festival itself. An annonce naming
    // an edition the app has not fetched is not an error here - the app drops it.
    if (!('editionId' in a)) errors.push(`annonce ${a.id}: editionId must be present, null when festival-wide`);
    else if (a.editionId !== null && a.editionId !== ed.id)
      warns.push(`annonce ${a.id}: editionId ${a.editionId} is not the current edition`);
    // A plain nullable URL rather than a typed internal action. An annonce is a dated record, and
    // the one thing it needs to do is open somewhere; null simply means it is not tappable.
    if (!('url' in a)) errors.push(`annonce ${a.id}: url must be present, null when the annonce is not tappable`);
    else if (a.url !== null) checkUrl(a.url, `annonce ${a.id}`);
    if (!PROVENANCE.has(a.provenance)) errors.push(`annonce ${a.id}: bad provenance`);
  }
  if (!ann.announcements.length) warns.push('announcements.json: no annonces yet');
}

// --- faq ------------------------------------------------------------------------------------
const faqIds = new Set();
for (const f of fest.faq || []) {
  if (faqIds.has(f.id)) errors.push(`faq ${f.id}: duplicate id`);
  faqIds.add(f.id);
  if (!f.question || !f.answer) errors.push(`faq ${f.id}: needs a question and an answer`);
  if (!PROVENANCE.has(f.provenance)) errors.push(`faq ${f.id}: bad provenance`);
}
if (!(fest.faq || []).length) warns.push('festival.json: faq is empty');

// --- categories ---------------------------------------------------------------------------
const catIds = new Set();
for (const c of ed.categories) {
  if (catIds.has(c.id)) errors.push(`category ${c.id}: duplicate id`);
  catIds.add(c.id);
  if (!c.name || typeof c.order !== 'number') errors.push(`category ${c.id}: needs a name and an order`);
}

// --- happenings ---------------------------------------------------------------------------
for (const [hid, h] of Object.entries(haps)) {
  if (!CATEGORIES.has(h.category)) errors.push(`happening ${hid}: category "${h.category}" is not declared in categories[]`);
  if (!PROVENANCE.has(h.provenance)) errors.push(`happening ${hid}: bad provenance ${h.provenance}`);
  if (!(h.kind in KIND_CATEGORIES)) errors.push(`happening ${hid}: bad kind ${h.kind}`);
  else {
    if (!(h.kind in h)) errors.push(`happening ${hid}: kind=${h.kind} but no '${h.kind}' payload`);
    if (CATEGORIES.has(h.category) && !KIND_CATEGORIES[h.kind].includes(h.category))
      errors.push(`happening ${hid}: kind "${h.kind}" cannot carry category "${h.category}" - allowed: ${KIND_CATEGORIES[h.kind].join(', ')}`);
  }
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

  const payload = h[h.kind] || {};

  if ('genres' in payload) {
    if (!Array.isArray(payload.genres)) errors.push(`happening ${hid}: genres must be an array`);
    else if (payload.genres.some(g => typeof g !== 'string')) errors.push(`happening ${hid}: genres must be strings`);
  }
  for (const l of payload.links || []) {
    if (!LINK_TYPES.has(l.type)) errors.push(`happening ${hid}: unknown link type ${l.type}`);
    checkUrl(l.url, `happening ${hid}/link ${l.type}`);
  }
  if (h.kind === 'artist' && (payload.links || []).length === 0)
    warns.push(`artist ${hid}: no links`);

  for (const m of payload.marks || [])
    if (!MARKS.has(m)) errors.push(`happening ${hid}: unknown stand mark "${m}"`);

  // Price is ONE shape for every activity, free or not. It used to be three mutually exclusive
  // ones - {free}, {amount,currency,per} and {tiers,deposit} - which meant the app had to sniff
  // which it was holding before it could read a number. The cost of that lands on every screen
  // that shows a price; the cost of this lands here, once.
  //
  // free and tiers are two views of one fact, so they are checked against each other rather than
  // independently: free:true with a tier, or free:false with none, is a content bug that would
  // otherwise render as "gratuit - CHF 10".
  if (h.kind === 'activity' && payload.price) {
    const pr = payload.price;
    const where = `activity ${hid}/price`;
    if (typeof pr.free !== 'boolean') errors.push(`${where}: free must be a boolean`);
    if (!Array.isArray(pr.tiers)) errors.push(`${where}: tiers must be an array, empty when free`);
    else {
      if (pr.free === true && pr.tiers.length > 0) errors.push(`${where}: free but carries ${pr.tiers.length} tier(s)`);
      if (pr.free === false && pr.tiers.length === 0) errors.push(`${where}: not free but carries no tier`);
      pr.tiers.forEach((t, i) => {
        if (typeof t.amount !== 'number') errors.push(`${where}/tiers[${i}]: amount must be a number`);
        if (!CURRENCIES.has(t.currency)) errors.push(`${where}/tiers[${i}]: unknown currency ${t.currency}`);
        if (!('label' in t)) errors.push(`${where}/tiers[${i}]: label must be present, null when there is one price for everyone`);
        if (!('per' in t)) errors.push(`${where}/tiers[${i}]: per must be present, null when the price is per person`);
        if (t.per !== null && !PRICE_UNITS.has(t.per)) errors.push(`${where}/tiers[${i}]: unknown per "${t.per}"`);
      });
    }
    if (!('deposit' in pr)) errors.push(`${where}: deposit must be present, null when there is none`);
    if (pr.deposit) {
      if (typeof pr.deposit.amount !== 'number') errors.push(`${where}/deposit: amount must be a number`);
      if (!CURRENCIES.has(pr.deposit.currency)) errors.push(`${where}/deposit: unknown currency`);
      if (!('note' in pr.deposit)) errors.push(`${where}/deposit: note must be present, null when there is none`);
    }
    if (!PROVENANCE.has(pr.provenance)) errors.push(`${where}: bad provenance`);
  }

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
        // Always an array, empty when the item has no mark. An absent array and an empty one said
        // the same thing, which meant every reader had to handle both to learn nothing.
        if (!Array.isArray(it.marks))
          errors.push(`stand ${hid}/${g.id}/${it.name}: marks must be an array, empty when there are none`);
        else for (const m of it.marks)
          if (!MARKS.has(m)) errors.push(`stand ${hid}/${g.id}/${it.name}: unknown mark "${m}"`);
        if (!('description' in it))
          errors.push(`stand ${hid}/${g.id}/${it.name}: description must be present, null when there is none`);
      }
    }
    if ((payload.menu || []).length === 0) warns.push(`stand ${hid}: no menu`);
  }
}

// --- slots --------------------------------------------------------------------------------
const seen = new Set();
for (const s of ed.slots) {
  const sid = s.id;
  if (seen.has(sid)) errors.push(`slot ${sid}: duplicate id`);
  seen.add(sid);
  if (!sid.startsWith('2026:')) errors.push(`slot ${sid}: id is not Edition-qualified`);
  if (!(s.happeningId in haps)) errors.push(`slot ${sid}: unknown happeningId ${s.happeningId}`);
  if (!(s.dayId in days)) errors.push(`slot ${sid}: unknown dayId ${s.dayId}`);
  if (!PROVENANCE.has(s.provenance)) errors.push(`slot ${sid}: bad provenance ${s.provenance}`);
  if ('laneId' in s || 'venueId' in s) errors.push(`slot ${sid}: laneId/venueId are gone`);

  // Every Slot is timed. "All day" is not a state the app models: a Happening that runs for the
  // whole festival gets its day's opening hours written out like any other Slot, so that no
  // screen has to format an absent time and no Slot is unplaceable on the Programme. Enforced
  // here rather than left to the app, because a null that reaches a mapper is a crash or a
  // blank row, and the fix belongs in the content.
  if (!s.start) errors.push(`slot ${sid}: no start - "all day" is written out as the day's opening hours`);
  if (!s.end) errors.push(`slot ${sid}: no end - "all day" is written out as the day's opening hours`);

  const st = ts(s.start), en = ts(s.end);
  if (st && en && en <= st) errors.push(`slot ${sid}: end is not after start`);
  const d = days[s.dayId];
  if (st && d) {
    if (st < ts(d.start)) warns.push(`slot ${sid}: starts ${s.start.slice(11, 16)}, before the site opens at ${d.start.slice(11, 16)}`);
    if (en && en > ts(d.end)) warns.push(`slot ${sid}: ends after closing time ${d.end.slice(11, 16)}`);
  }
}

// A Stand's Slots are its opening windows, and nobody has published them - so a Stand with no
// Slots is a known gap. An Artist or Activity without one is a real fault.
for (const [hid, h] of Object.entries(haps)) {
  if (ed.slots.some(s => s.happeningId === hid)) continue;
  if (h.kind === 'stand') warns.push(`stand ${hid}: no opening hours`);
  else errors.push(`happening ${hid}: has no slots`);
}

// One music stage: no two "musique" slots may overlap on the same day. This replaces the old
// lane+venue check and is the stronger rule - it states the actual physical constraint rather
// than relying on a presentation axis to encode it.
const musique = ed.slots
  .filter(s => s.start && s.end && haps[s.happeningId] && haps[s.happeningId].category === 'musique')
  .sort((a, b) => ts(a.start) - ts(b.start));
for (let i = 1; i < musique.length; i++)
  if (ts(musique[i].start) < ts(musique[i - 1].end))
    errors.push(`two musique slots overlap on one stage: ${musique[i - 1].id} vs ${musique[i].id}`);

const crossers = ed.slots
  .filter(s => s.start && s.end && s.start.slice(0, 10) !== s.end.slice(0, 10))
  .map(s => s.id);

// --- partners -----------------------------------------------------------------------------
const partnerIds = new Set();
const allMembers = ed.partners.flatMap(t => t.members || []);
for (const tier of ed.partners) {
  if (!PROVENANCE.has(tier.provenance)) errors.push(`partner tier ${tier.id}: bad provenance`);
  if (!(tier.members || []).length) warns.push(`partner tier ${tier.id}: no members`);
  for (const m of tier.members || []) {
    if (partnerIds.has(m.id)) errors.push(`partner ${m.id}: duplicate id across tiers`);
    partnerIds.add(m.id);
    if (!m.name) errors.push(`partner ${m.id}: no name`);
    if (m.url === undefined) errors.push(`partner ${m.id}: url must be present, use null if unknown`);
    else if (m.url !== null) checkUrl(m.url, `partner ${m.id}/url`);
    // A partner has a logo, not a photo: never cropped, never behind a scrim. Keeping the field
    // name distinct from "images" is what stops one being rendered as the other.
    if (m.logo === undefined) errors.push(`partner ${m.id}: logo must be present, use null if unknown`);
    else if (m.logo !== null) checkSrc(m.logo, `partner ${m.id}/logo`);
    if (m.url === null) warns.push(`partner ${m.id}: no website found`);
  }
}

// --- cross-file ---------------------------------------------------------------------------
if (!idx.editions.some(e => e.id === ed.id)) errors.push(`index.json does not list edition ${ed.id}`);
for (const e of idx.editions)
  if (!fs.existsSync(path.join(root, e.path))) errors.push(`index.json: path ${e.path} does not exist`);

const emailIds = new Set(fest.contact.emails.map(e => e.id));
for (const [p, v] of [
  ['accessibilite.contactEmailId', fest.accessibilite.contactEmailId],
  ['besoin.lostPropertyEmailId', fest.besoin.lostPropertyEmailId],
  ['simpliquer.hotstaff.contactEmailId', fest.simpliquer.hotstaff.contactEmailId],
  ['simpliquer.partenaire.contactEmailId', fest.simpliquer.partenaire.contactEmailId],
]) if (!emailIds.has(v)) errors.push(`festival.json ${p}: unknown email id ${v}`);

if (fest.currentEditionId !== ed.id)
  warns.push(`festival.json currentEditionId=${fest.currentEditionId} but only edition ${ed.id} authored`);

// Transport modes vary too much to share one shape beyond this: a name, some prose, and any
// number of links. Walking is prose alone; the bus needs a timetable link; a night shuttle laid
// on for one edition would be an Edition-level addition, not a mode here.
for (const m of fest.transports.modes || []) {
  if (!m.id || !m.name) errors.push(`transport ${m.id}: needs an id and a name`);
  if (m.body === null) warns.push(`transport ${m.id}: no text yet`);
  for (const l of m.links || []) {
    if (!l.label) errors.push(`transport ${m.id}: link without a label`);
    checkUrl(l.url, `transport ${m.id}/link`);
  }
}

// accepted is a boolean, never "unknown": a payment method whose status nobody knows is left
// out entirely rather than rendered as a shrug. TWINT in particular is the question a Swiss
// visitor actually asks, so a wrong or vague answer is worse than the FAQ saying it is being
// checked.
for (const m of fest.paiement.methods || []) {
  if (!m.id || !m.name) errors.push(`paiement ${m.id}: needs an id and a name`);
  if (typeof m.accepted !== 'boolean') errors.push(`paiement ${m.id}: accepted must be true or false`);
}
if (!(fest.paiement.methods || []).length) warns.push('festival.json: paiement has no methods');

// Accessibility records what is NOT available as deliberately as what is - "no accessible
// toilets" is information someone needs before travelling, and silence is not.
for (const i of fest.accessibilite.items || []) {
  if (!i.id || !i.name) errors.push(`accessibilite ${i.id}: needs an id and a name`);
  if (typeof i.available !== 'boolean') errors.push(`accessibilite ${i.id}: available must be true or false`);
}
if (!(fest.accessibilite.items || []).length) warns.push('festival.json: accessibilite has no items');

// Counted rather than listed: one line per missing image would drown every other finding.
const noImage = ed.happenings.filter(h => !(h.images || []).length);
const noLogo = allMembers.filter(m => !m.logo);
if (noImage.length) warns.push(`${noImage.length}/${ed.happenings.length} happenings have no image`);
if (noLogo.length) warns.push(`${noLogo.length}/${allMembers.length} partners have no logo`);

// Picture-bank integrity. shared/ is append-only by convention, and these two checks are what
// notice when it is not: a reference with no file behind it, and a file nothing points at.
if (PUBLISHED_BASE) {
  for (const rel of referenced)
    if (!/^https?:\/\//.test(rel) && !fs.existsSync(path.join(root, rel)))
      errors.push(`image reference has no file: ${rel}`);

  const walk = dir => fs.existsSync(path.join(root, dir))
    ? fs.readdirSync(path.join(root, dir), { withFileTypes: true })
        .flatMap(e => e.isDirectory() ? walk(`${dir}/${e.name}`) : (e.name === '.gitkeep' ? [] : [`${dir}/${e.name}`]))
    : [];
  for (const f of walk('shared'))
    if (!referenced.has(f)) warns.push(`unreferenced file in the picture bank: ${f}`);
} else if (referenced.size) {
  warns.push(`${referenced.size} image references cannot be checked - set PUBLISHED_BASE in validate.js`);
}

console.log(`slots=${ed.slots.length}  happenings=${ed.happenings.length}  days=${ed.days.length}  categories=${ed.categories.length}  partners=${allMembers.length}`);
console.log(`midnight-crossing slots: ${JSON.stringify(crossers)}`);
console.log(`\nERRORS (${errors.length}):`); errors.forEach(e => console.log('  x', e));
console.log(`\nWARNINGS (${warns.length}):`); warns.forEach(w => console.log('  !', w));
process.exit(errors.length ? 1 : 0);
