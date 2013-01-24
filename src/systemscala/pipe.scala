/*
* Copyright 2013 The SystemScala Authors. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package systemscala

object PipeMgr{
  abstract class PipeMgr[T] {
    var insts: scala.collection.mutable.HashMap[String, Pipe[T]]
  }
  implicit object PipeInt extends PipeMgr[Int]{ 
    var insts = scala.collection.mutable.HashMap[String, Pipe[Int]]()
  }
  implicit object PipeString extends PipeMgr[String]{ 
    var insts = scala.collection.mutable.HashMap[String, Pipe[String]]()
  }
  //TODO More types
}
  
import PipeMgr._

class Pipe[T](val name: String, val parent: Component = Component.root)
  (implicit pm: PipeMgr[T]) {
  import scala.util.continuations.cps
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var pipe = scala.collection.mutable.Queue[T]()
  Pipe.add[T](this)(pm)
  Event(fullname + "." + "read")
  Event(fullname + "." + "write")
  Event(fullname + "." + "changed")
  def event(n: String): Event = {
    Event.event(fullname + "." + n)
  }
  def onRead = event("read")
  def onWrite = event("write")
  def read: T@cps[Unit] = {
    onRead._notify
    if(pipe.isEmpty){
      onWrite._wait()
      pipe.dequeue
    } else {
      pipe.dequeue
    }
  }
  def write(v: T): Unit = {
    onWrite._notify
    pipe += v
  }
}

object Pipe{
  def apply[T](name: String, parent: Component = Component.root)
    (implicit pm: PipeMgr[T]) : Pipe[T] ={
    new Pipe[T](name, parent)(pm)
  }
  def add[T](s: Pipe[T])(implicit pm: PipeMgr[T]) {
    pm.insts += (s.fullname -> s)
  }
  def pipe[T](n: String)(implicit pm: PipeMgr[T]) : Pipe[T] = {
    pm.insts.get(n) match {
      case None => throw new Exception("Cannot find Pipe " + n)
      case Some(p) => p
    }
  }
}