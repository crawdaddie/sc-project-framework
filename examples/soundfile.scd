~soundfile = SoundFile.openRead(~path);
~bufRateScale = ~soundfile.sampleRate / Server.default.sampleRate;
// "bufRateScale for sample % is %".format(~path, ~bufRateScale).postln;

~buffer = Buffer.read(
  Server.local,
  ~path,
);

~defaultEvent = M { arg ev = ();
  (buf: ~buffer, startPos: 0).putAll(ev);
};

