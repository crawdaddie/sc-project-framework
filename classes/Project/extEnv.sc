+ Env {
  *fromArray { arg a;
    var size = a[1];
    var levels = Array.newClear(size + 1);
    var times = Array.newClear(size);
    var curves = Array.newClear(size);
    var releaseNode, loopNode;

    levels[0] = a[0];
    releaseNode = if(a[2] == -99, { nil }, { a[2] });
    loopNode = if(a[3] == -99, { nil }, { a[2] });
    size.do { arg i;
      levels[i + 1] = a[4 * (i + 1)];
      times[i]      = a[1 + (4 * (i + 1)) ];
      curves[i]     = a[3 + (4 * (i + 1)) ];
    };
    ^Env(levels: levels, times: times, curve: curves, releaseNode: releaseNode, loopNode: loopNode);
  }
}
