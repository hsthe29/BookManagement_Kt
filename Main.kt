package book_management

import java.io.IOException
import kotlin.jvm.Throws

@Throws(IOException::class)
fun main(){
    val prompt: String = """
                -----------------------------------
                1. list all books
                2. add a new book
                3. edit book
                4. delete a book
                5. search books by name
                6. sort books descending by price
                0. save & exit
                -----------------------------------
                Your option: """

    var onLoop = true
    var lst: ArrayList<Book>
    var temp: Book?

    val bookManager = BookManager()
    while (onLoop) {
        print(prompt)
        when (readln().toInt()) {
            0 -> {
                bookManager.saveToFile()
                println("""
                    Saving to file...
                    Bye!""")
                onLoop = false
            }
            1 -> bookManager.printBooks(bookManager.getBooks())
            2 -> {
                val (id, name, price) = getInput()
                if (bookManager.add(Book(id, name, price)))
                    println("Added successfully.") else println("Duplicated ID")
            }
            3 -> {
                val (id, name, price) = getInput()
                temp = bookManager.getBookById(id)
                if (temp == null) println("Invalid ID!") else {
                    temp.name = name
                    temp.price = price
                    println("Edit successfully.")
                }
            }
            4 -> {
                print("Enter book id: ")
                val id = readln().toInt()
                val t = bookManager.getBookById(id)
                if (t == null) println("Invalid ID!") else {
                    bookManager.remove(t)
                    println("Deleted successfully!")
                }
            }
            5 -> {
                print("Enter keyword: ")
                val keyword = readln()
                lst = bookManager.searchByName(keyword)
                if (lst.size == 0) println("(empty)") else {
                    bookManager.printBooks(lst)
                }
            }
            6 -> bookManager.sortDescByPrice()
            else -> println("Invalid option!")
        }
    }
}

fun getInput(): Triple<Int, String, Double> {
    print("Enter book id: ")
    val id = readln().toInt()
    print("Enter book name: ")
    val name = readln()
    print("Enter book price: ")
    val price = readln().toDouble()
    return Triple(id, name, price)
}