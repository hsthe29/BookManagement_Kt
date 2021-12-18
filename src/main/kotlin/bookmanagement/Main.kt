package bookmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay


@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    var flag by remember { mutableStateOf(1) }
    val bookMan = BookManager()
    var isOpen by remember { mutableStateOf(true) }

    if(isOpen){
        Window(title = "Book Management",
            state = rememberWindowState(height = 500.dp, width = 900.dp),
            onCloseRequest = { isOpen = false }, icon = painterResource("sample.png")
        ) {
            MenuBar {
                Menu(text = "File", mnemonic = 'F') {
                    Item(text = "All Books", onClick = {flag = 1})
                    Item(text = "Add Book", onClick = { flag = 2 })
                    Item(text = "Edit Book", onClick = { flag = 3 })
                    Item(text = "Delete", onClick = { flag = 4 })
                    Item(text = "Search", onClick = {flag = 5})
                    Item(text = "Sort", onClick = { flag = 6 })
                    Item("Exit", onClick = {bookMan.saveToFile()
                        isOpen = false }, shortcut = KeyShortcut(Key.Escape), mnemonic = 'E')
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                var isDialogOpen by remember { mutableStateOf(false) }
                when (flag) {
                    1 -> TableScreen(bookMan.getBooks())
                    2 -> {

                        val triple = TableInput()

                        Button(modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(20.dp), onClick = { isDialogOpen = true }) {
                            Text("Add")
                        }

                        if (isDialogOpen) {
                            Dialog(title = "Add Book",
                                onCloseRequest = {
                                    isDialogOpen = false
                                    flag = 1
                                }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        if (bookMan.add(
                                                Book(triple.first,
                                                    triple.second,
                                                    triple.third)))
                                            "Added successfully!" else "Duplicated ID!"
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        val triple = TableInput()
                        val bk = bookMan.getBookById(triple.first)

                        Button(modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(20.dp), onClick = { isDialogOpen = true
                            if(bk != null) {
                                bk.name = triple.second
                                bk.price = triple.third
                            }}) {
                            Text("Edit")
                        }

                        if (isDialogOpen) {
                            Dialog(title = "Edit Book",
                                onCloseRequest = {
                                    isDialogOpen = false
                                    flag = 1
                                }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        if (bk != null)
                                            "Edited successfully!" else "Invalid ID!"
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        var iD = DeleteBook()
                        val bk = if (iD == 0) null else bookMan.getBookById(iD)
                        var text by remember { mutableStateOf("Invalid ID") }
                        Button(modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(20.dp), onClick = { if (bk != null) {
                                bookMan.remove(bk)
                                text = "Deleted Successfully"
                            }
                            isDialogOpen = true
                        }) {
                            Text("Delete")
                        }

                        if (isDialogOpen) {
                            Dialog(title = "Delete Book",
                                onCloseRequest = {
                                    isDialogOpen = false
                                    flag = 1
                                }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        text = text
                                    )
                                }
                            }
                        }
                    }
                    5 -> {
                        var keyword by remember { mutableStateOf("") }

                        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                            keyword = KeyWInput()
                            TableScreen(bookMan.searchByName(keyword))
                        }
                    }
                    6 -> {bookMan.sortDescByPrice()
                        suspend { delay(500) }
                        flag = 1
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    alig : TextAlign = TextAlign.Left,
    fontSize : TextUnit = TextUnit.Unspecified,
    fontStyle : FontStyle? = null,
    fontFamily: FontFamily? = null
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp),
        textAlign = alig,
        fontSize = fontSize,
        fontStyle = fontStyle
    )
}

@Composable
fun TableScreen(book : ArrayList<Book>) {
    // Just a fake data... a Pair of Int and String
    val tableData = book.map { Triple(it.id, it.name, it.price) }
    // Each cell of a column must have the same weight.
    val col1W = .2f // 30%
    val col2W = .6f // 70%
    val col3W = .2f
    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        // Here is the header
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "ID", weight = col1W, alig = TextAlign.Center)
                TableCell(text = "Name", weight = col2W, alig = TextAlign.Center)
                TableCell(text = "Price", weight = col3W, alig = TextAlign.Center)
            }
        }
        // Here are all the lines of your table.
        items (tableData) {
            val (id, name, price) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = id.toString(), weight = col1W, alig = TextAlign.Center)
                TableCell(text = name, weight = col2W)
                TableCell(text = price.toString(), weight = col3W)
            }
        }
    }
}

@Composable
fun TableInput(): Triple<Int, String, Double> {
    var id = -1
    var name = ""
    var price = 0.0

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
        var textID by remember { mutableStateOf("") }
        var textName by remember { mutableStateOf("") }
        var textPrice by remember { mutableStateOf("") }

        Text("Enter book ID:")
        TextField(value = textID,
            onValueChange = {textID = it},
            singleLine = true
        )
        Text("Enter book name:")
        TextField(value = textName,
            onValueChange = {textName = it},
            singleLine = true
        )
        Text("Enter book price:")
        TextField(value = textPrice,
            onValueChange = {textPrice = it},
            singleLine = true
        )
        id = if(textID.trim().isEmpty()) 0 else textID.trim().toInt()
        name = textName
        price = if(textPrice.trim().isEmpty()) 0.0 else textPrice.trim().toDouble()
    }
    return Triple(id, name, price)
}

@Composable
fun KeyWInput():String {
    var keyword by remember { mutableStateOf("") }
    Column(){
        Text(text = "Enter Keyword", Modifier.align(Alignment.CenterHorizontally))
        TextField(singleLine = true,
            value = keyword,
            onValueChange = { keyword = it }
        )
    }
    keyword = keyword.trim()
    return keyword
}

@Composable
fun DeleteBook():Int {
    var text by remember { mutableStateOf("") }
    Column(){
        Text(text = "Enter Book ID", Modifier.align(Alignment.CenterHorizontally))
        TextField(singleLine = true,
            value = text,
            onValueChange = { text = it }
        )
    }
    return if (text.trim().isNotEmpty()) text.trim().toInt() else 0
}