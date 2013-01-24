systemscala
===========

SystemScala is a scala version of SystemC

For example,

	import systemscala._

	// Define a sub component	
	class SubComp (name: String, parent: Component = null)
	  extends Component(name, parent)
	
	// Define a component
	class MyComp(name: String, parent: Component = null)
	  extends Component(name, parent) {

	  // Instance a sub component
	  val sub = new SubComp("a", this)

	  // A initial block
	  initial {
	    Logger.info("MyComp start")
	  }
	  
	  // Instance a signal
	  var s = new Signal("signal0", this)

	  // A always block
	  var i = 0
	  always(SimTime(1)){
	    i += 1
	    s.write(i)
	    if(i == 10){
	      Logger.info("MyComp stop")
	      Simulate.stop = true
	    }
	  }

	  // Another always block which wait on value changing of signal 's'
	  always(s.onChanged){
	    Logger.info(s.read)
	  }
	  Logger.info(s.fullname)
	}
	
	// Main
	object Main extends Simulate {
	  def run {
	    // Instance top component
	    new MyComp("comp")
	  }
	}
