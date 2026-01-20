import com.example.elormov.retrofit.endpoints.UsersInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketTimeoutException


object RetrofitClient {

    private var retrofit: Retrofit? = null
    lateinit var usersInterface: UsersInterface

    fun init(ip: String, puerto: Int) {
        retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:$puerto/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usersInterface = retrofit!!.create(UsersInterface::class.java)
    }
}

/*class RetrofitClient(
    private val ipServidor: String,
    private val puerto: Int
) {

    private var socket: Socket? = null
    private var salida: PrintWriter? = null
    private var entrada: BufferedReader? = null

    /**
     * Conecta con el servidor
     */
    fun conectar(): Boolean {
        return try {
            socket = Socket(ipServidor, puerto).apply {
                soTimeout = 5000
            }

            println("Conectado al servidor")

            salida = PrintWriter(socket!!.getOutputStream(), true)
            entrada = BufferedReader(InputStreamReader(socket!!.getInputStream()))

            // Leer mensaje de bienvenida
            val bienvenida = entrada?.readLine()
            println("Servidor dice: $bienvenida")

            true

        } catch (e: IOException) {
            println("Error de conexión: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Envía un mensaje al servidor
     */
    fun enviarMensaje(mensaje: String) {
        salida?.let {
            it.println(mensaje)
            it.flush()
            println("Mensaje enviado: $mensaje")
        }
    }

    /**
     * Recibe un mensaje del servidor
     */
    fun recibirMensaje(): String? {
        return try {
            entrada?.readLine()?.also {
                println("Respuesta recibida: $it")
            }
        } catch (e: SocketTimeoutException) {
            println("Timeout esperando respuesta del servidor")
            null
        } catch (e: IOException) {
            println("Error recibiendo mensaje: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Envía un mensaje y espera respuesta
     */
    fun enviarYRecibir(mensaje: String): String? {
        enviarMensaje(mensaje)
        return recibirMensaje()
    }

    /**
     * Cierra la conexión
     */
    fun desconectar() {
        try {
            socket?.close()
            salida?.close()
            entrada?.close()
            println("Desconectado del servidor")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun estaConectado(): Boolean {
        return socket?.isConnected == true && socket?.isClosed == false
    }
}*/
