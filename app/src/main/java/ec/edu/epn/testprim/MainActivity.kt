package ec.edu.epn.testprim

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.epn.testprim.ui.theme.TestprimTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear instancia del DatabaseHelper
        val dbHelper = DatabaseHelper(this)

        // Insertar datos de ejemplo si es necesario
        insertSampleData(dbHelper);

        setContent {
            TestprimTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val eventos = remember { mutableStateListOf<Evento>() }
                    val cursor = dbHelper.getAllEvents()
                    eventos.clear()
                    eventos.addAll(cursorToEventosList(cursor))

                    EventosGrid(
                        eventos = eventos,
                        modifier = Modifier.padding(innerPadding),
                        onItemClick = { direccion ->
                            val uri = Uri.parse("geo:0,0?q=$direccion")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(mapIntent)
                        }
                    )
                }
            }
        }


    }

    private fun insertSampleData(dbHelper: DatabaseHelper) {
        if (dbHelper.getAllEvents().count == 0) {
            dbHelper.insertEvent(
                name = "Trail en el Mirador de la Perdiz",
                address = "Mirador de la Perdiz",
                date = "31/10/2024",
                assistant = "30"
            )
            dbHelper.insertEvent(
                name = "Concurso de la mejor colada morada",
                address = "Estadio de San José",
                date = "2024-11-28",
                assistant = "120"
            )
            dbHelper.insertEvent(
                name = "Conmemoración Día de los difuntos",
                address = "Cementerio de San José",
                date = "2024-11-29",
                assistant = "30"
            )
            dbHelper.insertEvent(
                name = "Concurso de Guagua de pan",
                address = "Estadio de San José",
                date = "03/11/2024",
                assistant = "100"
            )
        }

    }

    private fun openMap(address: String) {
        val uri = "geo:0,0?q=${Uri.encode(address)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Manejar el caso en que no haya apps para manejar el intent
            println("No hay aplicaciones disponibles para manejar el Intent.")
        }
    }


    @SuppressLint("Range")
    private fun cursorToEventosList(cursor: Cursor): List<Evento> {
        val eventosList = mutableListOf<Evento>()
        if (cursor.count > 0 && cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                val direccion =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS))
                val fecha = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                val asistentes =
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSISTANT))
                eventosList.add(Evento(nombre, direccion, fecha, asistentes))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventosList
    }
}

data class Evento(
    val nombre: String,
    val direccion: String,
    val fecha: String,
    val asistentes: String
)

@Composable
fun EventosGrid(
    eventos: List<Evento>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado
        Text(
            text = "Eventos en San José por el feriado de noviembre",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Grid de 2 columnas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(eventos) { evento ->
                EventoItem(evento, onItemClick = onItemClick)
            }
        }
    }
}


@Composable
fun EventoItem(evento: Evento, onItemClick: (String) -> Unit) {

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onItemClick(evento.direccion) } // Handle click
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First row: Image + Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Image with rounded corners
                val imageResource = when (evento.nombre) { // Use event name for image mapping
                    "Trail en el Mirador de la Perdiz" -> R.drawable.mountain
                    "Concurso de la mejor colada morada" -> R.drawable.colada
                    "Conmemoración Día de los difuntos" -> R.drawable.cemetery
                    "Concurso de Guagua de pan" -> R.drawable.guagua
                    else -> R.drawable.guagua // Default image for unknown events
                }
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = "Imagen del evento ${evento.nombre}", // Descriptive contentDescription
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small) // Rounded corners
                )
                // Details of the event
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = evento.nombre,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = evento.direccion,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = evento.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                }
            }
            // Second row: Asistentes
            Text(
                text = "${evento.asistentes} asistentes",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }
    }
}


