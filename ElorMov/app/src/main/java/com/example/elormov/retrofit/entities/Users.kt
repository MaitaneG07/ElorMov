import com.example.elormov.retrofit.entities.Matriculaciones
import com.example.elormov.retrofit.entities.Reuniones
import com.example.elormov.retrofit.entities.Tipos
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Users(
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String,

    // OJO: Tu servidor envía la contraseña.
    // Por seguridad, idealmente no deberías enviarla de vuelta al cliente,
    // pero como tu entidad la tiene, debemos recibirla o ignorarla.
    @SerializedName("password")
    val password: String?,

    @SerializedName("nombre")
    val nombre: String?,

    @SerializedName("apellidos")
    val apellidos: String?,

    @SerializedName("dni")
    val dni: String?,

    @SerializedName("direccion")
    val direccion: String?,

    @SerializedName("telefono1")
    val telefono1: String?,

    @SerializedName("telefono2")
    val telefono2: String?,

    @SerializedName("argazkiaUrl") // Coincide con tu camelCase de Java
    val argazkiaUrl: String?,

    // RELACIÓN: Aquí usamos la clase Tipos que creamos arriba
    @SerializedName("tipos")
    val tipos: Tipos?,

    // FECHAS: Las recibimos como String para evitar errores.
    // Llegarán como "2023-10-25T14:30:00"
    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?,

// Lista de matriculaciones (Solo vendrá llena si es Alumno y el backend lo envía)
    @SerializedName("matriculaciones")
    val matriculaciones: List<Matriculaciones>?,

// Lista de reuniones (Solo vendrá llena si es Profesor y el backend lo envía)
// Nota: en tu SQL la tabla se llama 'reuniones', el JSON debería coincidir
    @SerializedName("reuniones")
 val reuniones: List<Reuniones>?

) : Serializable
