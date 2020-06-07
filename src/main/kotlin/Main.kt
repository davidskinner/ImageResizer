import net.coobird.thumbnailator.Thumbnails
import java.awt.Dimension
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.FileImageInputStream
import javax.imageio.stream.ImageInputStream

data class ImageType(var width: Int, var name: String)

class ImageThread( var image: File, var imageType: ImageType, var folder:String) : Thread() {
    override fun run() {
        Thumbnails.of(File(image.absolutePath))
            .size(imageType.width, imageType.width)
            .toFile(File(folder + "${image.nameWithoutExtension + imageType.name}.jpg"))
    }
}

fun main() {
    //
    // 5760L x 3840W R:1.5
    // 480L
    // 800L
    // 1000L
    // 1300L
    // 1900L

    /// /Users/davidskinner/Desktop/resize/capspire/
    val imageTypes = listOf(
        ImageType(550, "smol"),
        ImageType(1440, "thicc")
    )

    val folder = "/Users/davidskinner/Desktop/resize/capspire/"

    val dir = File(folder)

    // get rid of stupid DS_Store
    val files: List<File> = dir.listFiles().toMutableList().filter {  it.extension != "DS_Store" }

    for (f in files) {
        for (it in imageTypes) {
//            Thumbnails.of(File(f.absolutePath))
//                .size(it.width, it.width)
//                .toFile(File(folder + "${f.nameWithoutExtension + it.name}.jpg"))
            ImageThread(f,it,folder).start()
            println("ay")
        }
    }
}

/**
 * Thanks internet
 * Gets image dimensions for given file
 * @param imgFile image file
 * @return dimensions of image
 * @throws IOException if the file is not a known image
 */
@Throws(IOException::class)
fun getImageDimension(imgFile: File): Dimension? {
    val pos = imgFile.name.lastIndexOf(".")
    if (pos == -1) throw IOException("No extension for file: " + imgFile.absolutePath)
    val suffix = imgFile.name.substring(pos + 1)
    val iter: Iterator<ImageReader> = ImageIO.getImageReadersBySuffix(suffix)
    while (iter.hasNext()) {
        val reader: ImageReader = iter.next()
        try {
            val stream: ImageInputStream = FileImageInputStream(imgFile)
            reader.input = stream
            val width: Int = reader.getWidth(reader.minIndex)
            val height: Int = reader.getHeight(reader.minIndex)
            return Dimension(width, height)
        } catch (e: IOException) {
            print("Error reading: " + imgFile.absolutePath)
        } finally {
            reader.dispose()
        }
    }
    throw IOException("Not a known image file: " + imgFile.absolutePath)
}
