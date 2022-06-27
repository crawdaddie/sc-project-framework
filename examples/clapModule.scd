var clapsf;
SynthDef('bufplayer2', { arg 
  out = 0,
  buf = 0,
  gate = 1,
  startPos = 0,
  sustain = 1,
  amp = 1,
  atk = 0.0,
  decay = 0.01,
  rate = 1,
  loop = 0
  ;

  var sig = PlayBuf.ar(2, buf,
    rate: rate * BufRateScale.kr(buf),
    startPos: startPos * BufFrames.kr(buf),
    loop: loop 
  );

  var env = EnvGen.kr(
    Env.asr(atk, amp, decay),
    gate: gate,
    doneAction: 2
  );
  Out.ar(out, Pan2.ar(sig * env));
}).add;

clapsf = Import("./Clap.wav", expose: false, loader: './soundfile');
// import a soundfile as a 'private' resource
// the above object is also an instance of Mod, indexed by its path ('./Clap.wav')
// you can freely import it in other contexts without worrying about unnecessarily loading several copies
// of the same soundfile

~baseEvent = clapsf.defaultEvent((instrument: 'bufplayer2'));

~player = NodeProxy.audio(s, 2).quant_(4); // set up a resource to play things and replace
~player.play;

~pattern = M { arg ... pattern;
  ~player.source = Pchain(Pbind(*pattern), ~baseEvent);
};

~basicPattern = M {
  mod.pattern( // reuse pattern declared above
    Pbind('dur', 0.25), // pass a very basic pattern into the player
  );
};

