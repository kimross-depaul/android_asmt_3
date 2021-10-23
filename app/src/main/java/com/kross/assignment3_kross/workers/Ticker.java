package com.kross.assignment3_kross.workers;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;

public class Ticker {
    String symbol;

    Ticker(String _symbol) {
        symbol = _symbol;
    }

    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("   ");
            jsonWriter.beginObject();
            jsonWriter.name("symbol").value(this.symbol);
            jsonWriter.endObject();
            jsonWriter.close();
            return writer.toString();
        }catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
