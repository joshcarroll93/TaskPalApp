package joshcarroll.projects.android.taskpal.utils;


public class Utils {

    public String formatAddress(String address){

        int amountOfCommas = 0;
        int position = 0;

        StringBuilder stringBuilder = new StringBuilder();

        char[] addressCharacters = address.toCharArray();

        for(int i = 0; i < addressCharacters.length; i++){

            if(addressCharacters[i] == ','){
                ++amountOfCommas;
            }

            if(amountOfCommas >= 3){
                position = i;
                break;
            }
        }
        stringBuilder.append(addressCharacters, 0, position);

        return stringBuilder.toString();
    }
}
