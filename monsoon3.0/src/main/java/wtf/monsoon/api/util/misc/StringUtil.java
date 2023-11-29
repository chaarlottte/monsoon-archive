package wtf.monsoon.api.util.misc;

import com.github.javafaker.Faker;
import org.luaj.vm2.ast.Str;

import java.util.Random;

/**
 * @author Surge
 * @since 30/07/2022
 */
public class StringUtil {

    public static String formatEnum(Enum<?> enumIn) {
        String text = enumIn.toString();
        StringBuilder formatted = new StringBuilder();

        try {
            if(enumIn.getClass().getMethod("toString").getDeclaringClass().toString().contains("monsoon")) {
                return text;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if(text.equalsIgnoreCase("ncp")) return "NCP";

        int index = 0;
        boolean isFirst = true, isNewWord = false;
        for (char c : text.toCharArray()) {
            if (c == '_') {
                isNewWord = true;
                continue;
            }

            if (isFirst) {
                if(String.valueOf(c).toLowerCase().equals(String.valueOf(c))) formatted.append(c);
                else formatted.append(String.valueOf(c).toUpperCase());
                isFirst = false;
            } else if(isNewWord) {
                if(String.valueOf(c).toLowerCase().equals(String.valueOf(c))) formatted.append(" ").append(c);
                else formatted.append(String.valueOf(c).toUpperCase());
                isNewWord = false;
            } else {
                formatted.append(String.valueOf(c).toLowerCase());
            }

            index++;
        }

        return formatted.toString();
    }

    public static String getRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * characters.length());
            salt.append(characters.charAt(index));
        }
        return salt.toString();
    }

    public static String getValidUsername() {
        Faker faker = new Faker();
        String username = faker.superhero().prefix() + faker.name().firstName() + faker.address().buildingNumber();
        System.out.println(username);
        return username;
    }

}