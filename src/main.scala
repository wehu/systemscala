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
  var s = new Signal("signal0", this)
  always(SimTime(1)){
    i += 1
    s.write(i)
    if(i == 10){
      Logger.info("MyComp stop")
      Simulate.stop = true
    }
  }
  always(s.event("changed")){
    Logger.info(s.read)
  }
  Logger.info(s.fullname)
}

object Main extends Simulate {
  def run {
    new MyComp("comp")
  }
}