package com.starrepublic.meetrix.utils

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by richard on 2016-11-10.
 */

class ObjectSerializer {
    companion object {
        fun serialize(obj: Any?) : String {
            if (obj == null) {
                return ""
            }

            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(obj)
            oos.close()

            return Base64.encodeToString(baos.toByteArray()!!, Base64.DEFAULT)
        }

        fun <T> deserialize(str: String?) : T? {
            if (str == null || str.isEmpty()) {
                return null
            }

            val bais = ByteArrayInputStream(Base64.decode(str,Base64.DEFAULT))
            val ois = ObjectInputStream(bais)

            @Suppress("UNCHECKED_CAST")
            return ois.readObject() as T
        }
    }
}
