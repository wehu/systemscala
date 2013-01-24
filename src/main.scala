import systemscala._

import helper._

class SubComp (name: String, parent: Component = root)
  extends Component(name, parent)

class MyComp(name: String, parent: Component = root)
  extends Component(name, parent) {
  val sub = new SubComp("a", this)
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
}

object Main extends Simulate {
  def run{
    new MyComp("comp0")
  }
}