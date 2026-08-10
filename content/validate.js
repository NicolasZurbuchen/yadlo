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

const CATEGORIES = new Set(['musique', 'eau', 'terre', 'enfants', 'silent']);
const PROVENANCE = new Set(['confirmed', 'archived', 'unverified']);
const ts = s => (s ? new Date(s) : null);

for (const [lid, l] of Object.entries(lanes))
  if (!sections.has(l.sectionId)) errors.push(`lane ${lid}: unknown sectionId ${l.sectionId}`);

for (const [hid, h] of Object.entries(haps)) {
  if (!CATEGORIES.has(h.category)) errors.push(`happening ${hid}: unknown category ${h.category}`);
  if (!PROVENANCE.has(h.provenance)) errors.push(`happening ${hid}: bad provenance ${h.provenance}`);
  if (!['artist', 'activity', 'stand'].includes(h.kind)) errors.push(`happening ${hid}: bad kind ${h.kind}`);
  else if (!(h.kind in h)) errors.push(`happening ${hid}: kind=${h.kind} but no '${h.kind}' payload`);
  if (!h.name || !h.name.fr) errors.push(`happening ${hid}: name has no 'fr'`);
}

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

for (const hid of Object.keys(haps))
  if (!ed.slots.some(s => s.happeningId === hid)) warns.push(`happening ${hid}: has no slots`);

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
