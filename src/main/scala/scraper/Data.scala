package scraper

case class Data(
 val url:String,
 //val stars:Int,
 //TODO: watchers, forks, ...
 val buildSystem: Set[BuildSystem]
)

abstract sealed class BuildSystem

case object maven extends BuildSystem
case object gradle extends BuildSystem
case object ant extends BuildSystem
case object eclipse extends BuildSystem
//case object sbt extends BuildSystem

//TODO: project types: android, web, example, api
//TODO: single or multi, what even is the definition of a project

//TODO: handle language