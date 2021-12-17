package bookmanagement

import java.io.*


data class Book(val id: Int, var name: String, var price: Double){
    override fun toString() = "%5d %-45s %10.2f".format(this.id, this.name, this.price)
}

class BookManager {
    private val books = ArrayList<Book>()
    private val file = File("src/main/resources/books.txt")
    init {
        loadFromFile()
    }

    @Throws(IOException::class)
    private fun loadFromFile(){
        println("Loading...")
        val br = BufferedReader(FileReader(file.absolutePath))
        var line = br.readLine()
        while (line != null) {
            line = line.trim ()
            if (line.isNotEmpty()) {
                val tmp = line.split(regex = Regex("\\s+"))
                books.add(Book(tmp[0].toInt(),
                    tmp.slice(1 until tmp.size - 1).joinToString(" "),
                    tmp[tmp.size - 1].toDouble()))
            }
            line = br.readLine()
        }
        br.close()
    }

    fun getBooks() = this.books

    fun printBooks(books : ArrayList<Book>){
        if(books.size == 0){
            println("(empty)")
            return
        }
        println("%-5s %-45s %-10s\n".format("ID", "Name", "Price"))
        books.forEach { println(it) }
    }

    fun add(book : Book) : Boolean {
        if (book.id in this.books.map{it.id})
            return false
        this.books.add(book)
        return true
    }

    fun getBookById(id : Int) = if (id in this.books.map{it.id})
        this.books.filter { it.id == id }[0] else null

    fun remove(book : Book) = this.books.remove(book)

    fun sortDescByPrice() = this.books.sortByDescending { it.price }

    fun searchByName(keyword : String) = this.books
        .filter { keyword.lowercase() in it.name.lowercase() }
        .toCollection(ArrayList())

    @Throws(IOException::class)
    fun saveToFile() {
        val bw = BufferedWriter(FileWriter(file))
        books.forEach {bw.write("$it\n") }
        bw.close()
    }
}
