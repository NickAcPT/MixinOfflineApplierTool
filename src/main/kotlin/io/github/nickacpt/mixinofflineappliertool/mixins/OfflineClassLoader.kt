package io.github.nickacpt.mixinofflineappliertool.mixins

import java.net.URL
import java.net.URLClassLoader

class OfflineClassLoader : URLClassLoader(emptyArray()) {
    public override fun addURL(url: URL) {
        super.addURL(url)
    }

    @Throws(ClassNotFoundException::class)
    public override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }


}