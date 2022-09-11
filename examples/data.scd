(
TempoClock.default.tempo = 130/60;
'./d'.import;
'./smoothCriminal'.import; // modules can be used to decouple note pattern generation from sound generation resources and graph definitions

)
~d.path
~d.edit; // open an NdefGui and play with the squelch control.
// Use ctl-s shortcut to save to a local file when happy with the sound
~d.pattern(
  'dur', Prand([1.5, 0.5, 0.25], inf),
  'note', Pseq([0, 2, 3, 5, 7, 8, 10], inf),
  'octave', Prand([2, 4], inf), 
  'squelch', Pwhite(0.06, 1.2, inf)
);

~d.bass.set('squelch', nil)
~d.path
~d.bassParams.squelch = 0.2
~d.bassParams.writeMinifiedTextArchive()
~d.bassParams.apply
~d.save;

~d.userData.writeMinifiedTextArchive("/Users/adam/projects/sc/sc-project-framework/examples/.d.data.scd")

(data: (bassParams: ~d.bassParams)).writeMinifiedTextArchive("/Users/adam/projects/sc/sc-project-framework/examples/.d.data.scd")
a = "./abc"
a[1..<Plug>(scnvim-show-signature)jk<Plug>(scnvim-show-signature)jkk]
a = (a: 1)
a.putAll((a: 2, b: 1))


kjkkkjkjk

File.exists("/Users/adam/projects/sc/sc-project-framework/examples/.d.data.scd")
