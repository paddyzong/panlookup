package com.cardlookup.panlookup.tool;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CardRangeJsonGenerator {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();

        File file = new File("data/pres.json.data");
        file.getParentFile().mkdirs();

        try (OutputStream out = new FileOutputStream(file);
             JsonGenerator gen = factory.createGenerator(out)) {

            gen.useDefaultPrettyPrinter();
            gen.writeStartObject();

            gen.writeStringField("serialNum", "5780074");
            gen.writeStringField("messageType", "PRes");
            gen.writeStringField("dsTransID", UUID.randomUUID().toString());

            gen.writeArrayFieldStart("cardRangeData");

            long start = 4000020000000000L;
            long rangeSize = 999999L;

            for (int i = 0; i < 1000; i++) {
                long startRange = start + i * (rangeSize + 1);
                long endRange = startRange + rangeSize;

                gen.writeStartObject();
                gen.writeStringField("startRange", String.format("%016d", startRange));
                gen.writeStringField("endRange", String.format("%016d", endRange));
                gen.writeStringField("actionInd", "A");
                gen.writeStringField("acsEndProtocolVersion", "2.1.0");
                gen.writeStringField("threeDSMethodURL", "https://secure4.arcot.com/content-server/api/tds2/txn/browser/v1/tds-method");
                gen.writeStringField("acsStartProtocolVersion", "2.1.0");
                gen.writeArrayFieldStart("acsInfoInd");
                gen.writeString("01");
                gen.writeString("02");
                gen.writeEndArray();
                gen.writeEndObject();
            }

            gen.writeEndArray();
            gen.writeEndObject();
        }

        System.out.println("Streamed JSON file written to " + file.getPath());
    }
}
