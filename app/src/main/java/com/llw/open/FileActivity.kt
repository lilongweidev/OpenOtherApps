package com.llw.open

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.llw.open.databinding.ActivityFileBinding
import java.io.*
import java.util.*

class FileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onResume() {
        super.onResume()
        Log.d("FileActivity", "onResume: ${intent.data?.path}")
        binding.tvPath.text = intent.data?.path

        Toast.makeText(this,uriToFile(intent.data),Toast.LENGTH_SHORT).show()
    }

    /**
     * 通过URI得到文件信息，并保存当前应用文件夹下
     */
    private fun uriToFile(uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        //获得ContentResolver，用于访问其它应用数据
        val resolver: ContentResolver = contentResolver
        //获得URI路径
        val pathUri = uri.path!!.lowercase(Locale.getDefault())
        //获取文件名称
        val fileName = pathUri.substring(pathUri.lastIndexOf("/") + 1)
        //新文件的路径
        val filePath = getExternalFilesDir(null)!!.absolutePath
        //创建文件
        val file = File(filePath, fileName)
        val parentFile = file.parentFile
        if (parentFile != null) {
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
        }
        if (file.exists()) {
            return "文件已存在"
        }
        val inputStream: InputStream?
        return try {
            file.createNewFile()
            inputStream = resolver.openInputStream(uri)
            val outputStream = FileOutputStream(file.absolutePath)
            write(inputStream, outputStream)
            "文件已保存到本地。"
        } catch (e: IOException) {
            e.printStackTrace()
            "错误异常：" + e.message
        }
    }

    /**
     * 将输入流的数据拷贝到输出流
     */
    private fun write(inputStream: InputStream?, outputStream: OutputStream) {
        val buffer = ByteArray(1024 * 1024)
        while (true) {
            try {
                val len = inputStream!!.read(buffer)
                if (len < 0) break
                outputStream.write(buffer, 0, len)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            outputStream.flush()
            inputStream!!.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}