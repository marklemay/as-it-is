package scraper

import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URLEncodedUtils
import jpathwrapper.Jpath
import scala.io.Source
import scala.reflect.io.Path
import scala.reflect.io.File
import java.net.URLEncoder
import org.apache.commons.lang3.StringUtils.substringAfterLast

object Main {

  def jp[T](json: String, jp: String): List[T] = {

    import scala.collection.JavaConversions._

    return Jpath.jp[java.util.List[T]](json, jp).toList;
  }

   import Config._;
    

  
  def getBuilds(json:String):Set[BuildSystem] ={
    var buildSystems = Set[BuildSystem]();
    
              //"path": "pom.xml",
          //"type": "file", //TODO: confirm that each of these is a file

    
          val allpaths = jp[java.lang.String](json, "$.tree[*].path");

          
          val allnames =allpaths.map{s=>substringAfterLast("/"+s,"/")}.toSet

                  
          if (allnames.contains("pom.xml")) {
            buildSystems = buildSystems ++ Set(maven)
          }

          if (allnames.contains("build.gradle")) {
            buildSystems = buildSystems ++ Set(gradle)
          }

          if (allnames.contains("build.xml")) {
            buildSystems = buildSystems ++ Set(ant)
          }
          
          if (allnames.contains(".project")) {
            buildSystems = buildSystems ++ Set(eclipse)
          }
    
    return buildSystems
  }
    
  
  def getbuild(){
    
    var map  = Map[Set[BuildSystem],Int]().withDefault(x=>0)
    
    Source.fromFile(raw"C:\Users\christ\git\as-it-is\src\test\resources\java").getLines().foreach {
      ln =>
        {

          val part = ln.replaceFirst("https://github.com/", "")
          print(part)
          val url = "https://api.github.com/repos/" + part + "/git/trees/master?recursive=1&"+client //FIXME: use defualt branch, might not be master

          try{//FIXME: remove this block
          val response = Request.Get(url + "?"+client)
            .execute().returnContent();

          val buildSystems = getBuilds( response.asString())

print(buildSystems.mkString(" [","] [", "]"))
          
          map += buildSystems -> (map(buildSystems)+1)
println
val total:Float = map.map{case (s,n) =>n}.fold(0)(_+_)

          println(map.map{case (s,n) => s.mkString(" [","] [", "]")+":\t"+n+"\t("+ (n.asInstanceOf[Float]/total)+")"}.mkString("\n"))
          println

          //Thread.sleep(2000L) // rate limited
          }catch {
  case t => t.printStackTrace()// todo: handle error
}
          
        }
  }
  
    }
  def main(args: Array[String]) {
    println("hi?????")

    getbuild()


  }

  //FIXME: there is definitely a claner scala way to do this
//  TODO: need to get "master_branch" too
  def getall(lang: String, Stars: Int): List[String] = {
    var all_urls = List[String]();

    var again: Boolean = true;
    var i = 1;

    do {
      val response = Request.Get("https://api.github.com/search/repositories?"
        + "q=language:"
        //        + "scala"
        + "java"
        + "+stars:"
        + "%3E1"
        // >1
        + "&sort=stars&order=desc"
        + "&page=" + i
        //pagination starts at 1
        + "&per_page=100" //NOTE: they seem to cap us at 100
        )
        .execute().returnContent();

      //TODO: save this for record

      //println(response)

      val json = response.asString()

      import scala.collection.JavaConversions._

      val urls = Jpath.jp[java.util.List[String]](json, "$.items[*].html_url").toList;
      println(urls.mkString("\n"))
      all_urls ++= urls;

      again &&= (urls.size > 0);

      Thread.sleep(60000L / 5l) // rate limited
      //TODO: if we auth we can pump this up
      i += 1;
    } while (again)
    //TODO: how often do people actually use do...whiles?
    //did projects start with maven?

    return all_urls;
  }
}