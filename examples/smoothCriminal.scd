var r = Rest();
~verse = Pseq([
  r,  r,  0,  r,
  0,  0, -2,  0,
  2,  r,  2,  r,
  r,  r,  0,  2,
  3,  r,  3,  r,
  r,  r,  3,  3,
  2,  0, -2,  r,
  r,  r,  0,  r
], inf);

~verseVariation = Pseq([
   r,  r,  0,  r,
   0,  0,  0,  0,
  -2,  r, -2,  r,
   r,  r, -2, -2,
  -4,  r, -4,  r,
   r,  r, -4, -4,
  -5,  r, -5,  r,
  -2,  r,  0,  r,
  ~verse
]);

