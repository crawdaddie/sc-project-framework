// basic example
'./exampleModule'.import;
~exampleModule.exposedValue.postln; // -> 2 
~exampleModule.postPrivateValues; // -> [1] 

// more useful module that actually does something
~claps = './clapModule'.asModule; // load module but save it as ~claps rather than ~clapModule
~claps.pattern(
  ['dur', 'rate'], Pseq([
    [1, Rest()],
    [1, 1],
    [1, Rest()],
    [1, 1],
  ], inf)
); // pass in a simple beat

~claps.basicPattern; // use a 'preset' defined in the module


// more complex example using a data object to save user params
(
TempoClock.default.tempo = 130/60;
'./savingParams'.import;
'./smoothCriminal'.import; // modules can be used to decouple note pattern generation from sound generation resources and graph definitions

~savingParams.pattern(
  'dur', 0.25,
  'note', Pstep(~smoothCriminal.verse, 0.25), 
  'octave', 2 
);
)

~savingParams.edit; // open an NdefGui and play with the squelch control.
// Use ctl-s shortcut to save to a local file when happy with the sound

~savingParams.pattern(
  'dur', 0.25,
  'note', Pstep(~smoothCriminal.verseVariation, 0.25),
  'octave', 2 
);




'./dataVal'.load;
~dataVal
