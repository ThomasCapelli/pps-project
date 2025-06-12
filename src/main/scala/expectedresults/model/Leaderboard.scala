package expectedresults.model

import java.io.File
import scala.collection.immutable.TreeSet

trait Leaderboard {
  def submit(path: String, lines: Int): Leaderboard

  def toList: List[(String, Int)]

  def merge(leaderboard: Leaderboard): Leaderboard
}

object Leaderboard {
  def apply(leaderboard: TreeSet[(String, Int)], numLongestFiles: Int): Leaderboard =
    LeaderboardImpl(leaderboard, numLongestFiles)

  def apply(numLongestFiles: Int): Leaderboard = Leaderboard(TreeSet(), numLongestFiles)

  case class LeaderboardImpl(private val leaderboard: TreeSet[(String, Int)], private val numLongestFiles: Int) extends Leaderboard {
    override def submit(path: String, lines: Int): Leaderboard = {
      val tempLeaderboard = leaderboard + (path -> lines)
      if (leaderboard.size < numLongestFiles) {
        Leaderboard(tempLeaderboard, numLongestFiles)
      } else {
        Leaderboard(tempLeaderboard - leaderboard.minBy(_._2), numLongestFiles)
      }
    }

    override def toList: List[(String, Int)] = leaderboard.toList.sorted((a, b) => b._2 - a._2)

    override def merge(leaderboard: Leaderboard): Leaderboard = Leaderboard((this.toList ++ leaderboard.toList).sorted((a, b) => b._2 - a._2).take(numLongestFiles).to(TreeSet), numLongestFiles)

    override def toString: String = {
      val builder = new StringBuilder()
      toList.map(l => s"${new File(l._1).getName} with ${l._2} lines\n").foreach(builder.append)
      builder.toString()
    }
  }
}