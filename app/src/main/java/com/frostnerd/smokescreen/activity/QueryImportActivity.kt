package com.frostnerd.smokescreen.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.frostnerd.smokescreen.R
import com.frostnerd.smokescreen.database.converters.StringListConverter
import com.frostnerd.smokescreen.database.entities.DnsQuery
import com.frostnerd.smokescreen.database.getDatabase
import com.frostnerd.smokescreen.getPreferences
import com.frostnerd.smokescreen.showInfoTextDialog
import org.minidns.record.Record
import java.io.BufferedReader
import java.io.InputStreamReader

/*
 * Copyright (C) 2019 Daniel Wolf (Ch4t4r)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact the developer at daniel.wolf@frostnerd.com.
 */

class QueryImportActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getPreferences().theme.dialogStyle)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogactivity_server_import)
        supportActionBar?.hide()
        actionBar?.hide()

        if (intent != null) {
            val uri = intent.data
            if(uri != null) {
                if(uri.scheme == "content" || uri.scheme == "file") {
                    val resolver = contentResolver
                    val inputStream = resolver.openInputStream(uri)

                    val queries = BufferedReader(InputStreamReader(inputStream)).useLines {
                        val queries = mutableListOf<DnsQuery>()
                        val iterator = it.iterator()
                        if(iterator.hasNext()) {
                            iterator.next()
                            val converter = StringListConverter()
                            for(line in iterator) {
                                val split = line.split(",")
                                queries.add(DnsQuery(
                                    name=split[0],
                                    type =  Record.TYPE.getType(split[3].toInt()),
                                    askedServer = split[4],
                                    fromCache = split[5].toBoolean(),
                                    questionTime = split[6].toLong(),
                                    responseTime = split[7].toLong(),
                                    responses = converter.stringToList(split[8].replaceFirst("\"", "").replace(Regex("\"$"), ""))
                                ))
                            }
                        }
                        queries
                    }
                    val repo = getDatabase().dnsQueryRepository()
                    repo.insertAllAsync(queries)
                    showInfoTextDialog(this, "${queries.size} Queries imported.", "No text...")
                }
            }
        }
    }
}