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

class Pipe[T: Manifest](val name: String, val parent: Component = Component.root) {
  import scala.util.continuations.cps
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var pipe = scala.collection.mutable.Queue[T]()
  Pipe.add(fullname, this)
  override def toString() = "Pipe[" + manifest[T] + "] " + fullname
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
  var insts = scala.collection.mutable.HashMap[String, Any]()
  def apply[T: Manifest](name: String, parent: Component = Component.root) : Pipe[T] ={
    new Pipe[T](name, parent)
  }
  def add(fullname: String, s: Any) {
    insts.get(fullname) match {
      case None => insts += (fullname -> s)
      case _ => throw new Exception("Attempt to redefine a pipe " + fullname)
    }
  }
  def pipe[T](n: String) : Pipe[T] = {
    insts.get(n) match {
      case Some(p: Pipe[T]) => p
      case _ => throw new Exception("Cannot find Pipe " + n)
    }
  }
}