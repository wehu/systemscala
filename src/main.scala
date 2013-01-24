import systemscala._

class SubComp (name: String, parent: Component = null)
  extends Component(name, parent)

class MyComp(name: String, parent: Component = null)
  extends Component(name, parent) {
  val sub = new SubComp("a", this)
  initial {
    Logger.info("MyComp start")
  }
  var i = 0
  var s = Signal("signal0", this)
  always(SimTime(1)){
    i += 1
    s.write(i)
    if(i == 10){
      Logger.info("MyComp stop")
      Simulate.stop = true
    }
  }
  always(s.onChanged){
    Logger.info(s.read)
  }
  Logger.info(s.fullname)
  var p = Pipe("pipe0", this)
  initial {
    wait(SimTime(2))
    p.write("pipe a")
    wait(SimTime(2))
    p.write("pipe b")
    wait(SimTime(4))
    p.write("pipe c")
  }
  always(SimTime(1)){
    var d = p.read
    Logger.info(d)
  }
}

object Main extends Simulate {
  def run {
    new MyComp("comp")
  }
}