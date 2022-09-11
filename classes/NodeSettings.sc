NodeSettings : Environment {
  var nodeRef;
  *new { arg obj = (), nodeRef;
    ^super.new().init(obj, nodeRef)
  }
  init { arg obj, anodeRef;
    var node;
    nodeRef = anodeRef;
    this.putAll(obj);
    know = true;
    node = currentEnvironment[nodeRef];
    this.apply(node);
  }

  *serializeNodeValue { arg key, val;
    var x = case 
      // env node values are typically wrapped as: [env.asArray]
      // to prevent multichannel expansion
      { key.asString.contains("Env") } { Env.fromArray(val[0]) }
      { key.asString.contains("eq_controls") } { val.clump(3).collect({ |item| [ item[0], item[1], item[2] ]}) }
      { val };
    ^x;
  }
  *serializeNode { arg node;
    var dataDict = Dictionary
      .newFrom(node.getKeysValues.flatten)
      .collect({ arg val, key;
        this.serializeNodeValue(key, val);
      });
    ^dataDict;
  }
  
  *deserializeNodeValue { arg key, val;
    var x;
    x = case 
        { key.asString.contains("Env") } { [val.asArray] }
        { key.asString.contains("eq_controls") } {
          val.collect({ |item,i|
            [ item[0], item[1], item[2] ]
          }).flat;
        }
        { key == 'presets' } { nil }
        { val };
    ^x;
  }

  *deserializeNode { arg params;
    ^().putAll(params).collect({ arg val, key;
      if(key !== 'vol') { this.deserializeNodeValue(key, val) } { nil };
    })
  }

  deserialize {
    ^NodeSettings.deserializeNode(this).getPairs
  }

  serialize { arg node;
    var settings = NodeSettings.serializeNode(node).putAll((vol: node.vol));
    this.putAll(settings);
  }

  apply { arg node;
    ["apply", node].postln;
    if (node.notNil, {
      node.set(*this.deserialize);
      node.vol = this.vol;
    })
  }
}
