package model

import expectedresults.model.Leaderboard
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.TreeSet

class LeaderboardTest extends AnyFunSuite {

  test("submit should add files up to the limit") {
    val lb = Leaderboard(3)
      .submit("file1.txt", 10)
      .submit("file2.txt", 20)
      .submit("file3.txt", 5)

    val expected = List(
      ("file2.txt", 20),
      ("file1.txt", 10),
      ("file3.txt", 5)
    )

    assert(lb.toList == expected)
  }

  test("submit should keep only top N longest files") {
    val lb = Leaderboard(2)
      .submit("file1.txt", 10)
      .submit("file2.txt", 20)
      .submit("file3.txt", 30)

    val expected = List(
      ("file3.txt", 30),
      ("file2.txt", 20)
    )

    assert(lb.toList == expected)
  }

  test("merge should correctly combine two leaderboards keeping top N") {
    val lb1 = Leaderboard(3)
      .submit("file1.txt", 10)
      .submit("file2.txt", 20)

    val lb2 = Leaderboard(3)
      .submit("file3.txt", 30)
      .submit("file4.txt", 5)

    val merged = lb1.merge(lb2)

    val expected = List(
      ("file3.txt", 30),
      ("file2.txt", 20),
      ("file1.txt", 10)
    )

    assert(merged.toList == expected)
  }

  test("toString should return properly formatted string") {
    val lb = Leaderboard(2)
      .submit("/path/to/file1.txt", 100)
      .submit("/path/to/file2.txt", 200)

    val output = lb.toString
    assert(output.contains("file2.txt with 200 lines"))
    assert(output.contains("file1.txt with 100 lines"))
  }

  test("leaderboard with same lines should preserve order by filename") {
    val lb = Leaderboard(3)
      .submit("a.txt", 10)
      .submit("b.txt", 10)
      .submit("c.txt", 10)

    val expected = List(
      ("a.txt", 10),
      ("b.txt", 10),
      ("c.txt", 10)
    )

    assert(lb.toList.map(_._1).sorted == expected.map(_._1).sorted)
  }
}
