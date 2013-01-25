systemscala
===========

SystemScala is a scala version of SystemC

For example,

	import systemscala._

	import Helper._

	// Define a sub component	
	class SubComp (name: String, parent: Component = root)
	  extends Component(name, parent)
	
	// Define a component
	class MyComp(name: String, parent: Component = root)
	  extends Component(name, parent) {

	  // Instance a sub component
	  val sub = new SubComp("a", this)

	  // Instance a signal
	  var s = Signal[Int]("signal0", 0, this)

	  // A always block
	  var i = 0
	  always(1){
	    i += 1
	    s.write(i)
	    if(i == 10){
	      info("MyComp stop")
	      stop
	    }
	  }

	  // Another always block which wait on value changing of signal 's'
	  always(s.onChanged){
	    info(s.read)
	  }

	  // Instance a pipe
	  var p = Pipe[String]("pipe0", this)
	  initial {
	    delay(2)
	    p.write("pipe a")
	    delay(2)
	    p.write("pipe b")
	    delay(4)
	    p.write("pipe c")
	  }

	  // Repeat reading the pipe
	  repeat {
	    var d = p.read
	    info(d)
	  }
	}
	
	// Main
	object Main extends Simulate {
	  def run {
	    // Instance top component
	    new MyComp("comp")
	  }
	}
