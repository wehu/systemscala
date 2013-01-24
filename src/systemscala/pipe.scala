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

class Pipe(val name: String, val parent: Component = Component.root) {
  import scala.util.continuations.cps
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var pipe = scala.collection.mutable.Queue[Any]()
  Pipe.add(this)
  Event(fullname + "." + "read")
  Event(fullname + "." + "write")
  Event(fullname + "." + "changed")
  def event(n: String): Event = {
    Event.event(fullname + "." + n)
  }
  def onRead = event("read")
  def onWrite = event("write")
  def read: Any@cps[Unit] = {
    onRead._notify
    if(pipe.isEmpty){
      onWrite._wait()
      pipe.dequeue
    } else {
      pipe.dequeue
    }
  }
  def write(v: Any): Unit = {
    onWrite._notify
    pipe += v
  }
}

object Pipe{
  var insts = scala.collection.mutable.HashMap[String, Pipe]()
  def apply(name: String, parent: Component = Component.root) : Pipe ={
    new Pipe(name, parent)
  }
  def add[T](s: Pipe){
    insts += (s.fullname -> s)
  }
  def pipe(n: String): Pipe = {
    insts.get(n) match {
      case None => throw new Exception("Cannot find Pipe " + n)
      case Some(p) => p
    }
  }
}