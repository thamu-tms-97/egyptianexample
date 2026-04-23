package com.egyptianexample;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONFile {

  // read a json file and return an array
  public static JSONArray readArray(String fileName) {
    JSONParser jsonParser = new JSONParser();
    JSONArray data = null;

    try {
      InputStream inputStream = JSONFile.class.getClassLoader().getResourceAsStream(fileName);

      if (inputStream == null) {
        throw new RuntimeException("Could not find file: " + fileName);
      }

      Reader reader = new InputStreamReader(inputStream);
      Object obj = jsonParser.parse(reader);
      data = (JSONArray) obj;
      reader.close();

    } catch (ParseException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return data;
  }
}