import systemscala._

import Helper._

class SubComp (name: String, parent: Component = root)
  extends Component(name, parent)

class MyComp(name: String, parent: Component = root)
  extends Component(name, parent) {
  val sub = new SubComp("subcomp0", this)
  var i = 0
  var s = Signal[Int]("signal0", 0, this)
  always(1){
    i += 1
    s.write(i)
    if(i == 10){
      stop
    }
  }
  always(s.onChanged){
    info(s.read)
  }
  var p = Pipe[String]("pipe0", this)
  initial {
    delay(2)
    p.write("pipe a")
    delay(2)
    p.write("pipe b")
    delay(4)
    p.write("pipe c")
  }
  repeat {
    var d = p.read
    info(d)
  }
  initial {
    join(spawn{delay(3);info("spawn 0")},
         spawn{delay(4);info("spawn 1")})
    info("join")
  }
}

object Main extends Simulate {
  new MyComp("comp0")
  info(component("comp0.subcomp0"))
  info(signal[Int]("comp0.signal0"))
  info(pipe[String]("comp0.pipe0"))
}