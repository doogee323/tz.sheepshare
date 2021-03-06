package tz.basis.query.core.evaluator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 *
 * @author TZ
 *
 */
class CallMetaDataBuilder {

    private static Pattern sp = Pattern.compile("^\\{\\s*call\\s*([^\\(]*).*\\}$", Pattern.CASE_INSENSITIVE);

    private static Pattern fun = Pattern.compile("^\\{\\s*\\?\\s*=\\s*call\\s*([^\\(]*).*\\}$",
            Pattern.CASE_INSENSITIVE);

    /**
     *
     * @param statement
     * @return
     */
    public static boolean isCallStatement(String statement){
        return isProcedureStatement(statement) || isFunctionStatement(statement);
    }

    private static boolean isProcedureStatement(String statement){
        return sp.matcher(statement).find();
    }

    private static boolean isFunctionStatement(String statement){
        return fun.matcher(statement).find();
    }

    /**
     *
     * @param statement
     * @return
     */
    public static CallMetaData buildCallMataData(String statement){

        Pattern pattern = null;

        if(isFunctionStatement(statement)){
            pattern = fun;
        }else{
            pattern = Pattern.compile("^\\{\\s*call\\s*([^\\(]*).*\\}$", Pattern.CASE_INSENSITIVE);
        }

        Matcher matcher = pattern.matcher(statement);

        String callString = null;

        if(matcher.find()){
            callString = matcher.group(1);
        }

        if(StringUtils.hasText(callString)){
            String schemaName = null;
            String catalogName = null;
            String procedureName = callString;

            int index = callString.indexOf(".");
            if(index > -1){
                schemaName = callString.substring(0, index);
                procedureName = callString.substring(index + 1);
                int index2 = procedureName.indexOf(".");
                if(index2 > -1){
                    catalogName = procedureName.substring(0, index2);
                    procedureName = procedureName.substring(index2 + 1);
                }
            }

            return new CallMetaData(schemaName, catalogName, procedureName, fun == pattern);
        }

        return null;
    }

}
