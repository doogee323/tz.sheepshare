package tz.extend.core.persistence.file.policy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * ---------------------------------------------------------------
 * 업무구분 : TZ
 * 프로그램 :
 * 설    명 : 일반적인 DOS wildcard를 Java의 정규식 패턴으로 변경한다.
 *
 *         wildcard는 * 및 ?를 사용할 수 있다.
 *          ex) *.jsp a*.java b*.?sp
 *
 *         여러개의 wildcard를 사용할 경우 ;로 구분할 수 있다.
 *          ex) *.jap;*.java;*.class
 *
 *         wildcard는 path중 일부에도 적용할 수 있다.
 *          ex) /etc/sam*a/*.sh
 *
 *         이 클래스는 JDK V1.4이상에서 동작한다.
 * 작 성 자 : TZ
 * 작성일자 : 2013-02-01
 * 수정이력
 * ---------------------------------------------------------------
 * 수정일          이  름    사유
 * ---------------------------------------------------------------
 * 2013-02-01             최초 작성
 * ---------------------------------------------------------------
 * </pre>
 * @version 1.0
 */
public class WildcardAnalyzer {

    /**
     * <pre>
     * Don't let anyone instantiate this class
     * </pre>
     */
    private WildcardAnalyzer() {}

    /**
     * <pre>
     * Java Style의 정규식 패턴의 특수문자들을 escape 시키기 위하셔 정의한 정규식 패턴
     * </pre>
     */
    private static final Pattern escapePattern = Pattern.compile("(\\.|\\\\|\\[|\\]|\\^|\\$|\\+|\\{|\\}|\\(|\\)|\\|)");

    /**
     * <pre>
     * 도스 파일시스템의 wildcard중 *를 Java Style RegExp로 변경하기 위해 사용되는 정규식 패턴
     * </pre>
     */
    private static final Pattern asteriskPattern = Pattern.compile("\\*");

    /**
     * <pre>
     * 도스 파일시스템의 wildcard중 ?를 Java Style RegExp로 변경하기 위해 사용되는 정규식 패턴
     * </pre>
     */
    private static final Pattern questionmarkPattern = Pattern.compile("\\?");

    /**
     * <pre>
     * 여러개의 wildcard 적용시 seperator로 사용될 ;를 정의하기 위해 사용되는 정규식 패턴
     * </pre>
     */
    private static final Pattern multiplePattern = Pattern.compile("\\;");

    /**
     * <pre>
     * 주어진 wildcard를 분석하여 Java 정규식 패턴으로 리턴한다.<BR>
     * 대소문자 구분은 하지 않는다.
     * </pre>
     *
     * @param wildcard 분석 대상이 될 wildcard 문자열
     * @return Pattern 분석된 자바 정규식 패턴
     */
    public static Pattern compileWildcardPattern(String wildcard) {
        return compileWildcardPattern(wildcard, false);
    }

    /**
     * <pre>
     * 주어진 wildcard를 분석하여 Java 정규식 패턴으로 리턴한다.<BR>
     * </pre>
     *
     * @param wildcard 분석 대상이 될 wildcard 문자열
     * @param ignoreCase wildcard 분석시 대소문자 구분할지를 결정 (true:대소문자 구분)
     * @return Pattern 분석된 자바 정규식 패턴
     */
    public static Pattern compileWildcardPattern(String wildcard, boolean ignoreCase) {

        Matcher escapeMatcher = escapePattern.matcher(wildcard);
        wildcard = escapeMatcher.replaceAll("\\\\$1");

        Matcher asteriskMatcher = asteriskPattern.matcher(wildcard);
        wildcard = asteriskMatcher.replaceAll("(.*)");

        Matcher questionmarkMatcher = questionmarkPattern.matcher(wildcard);
        wildcard = questionmarkMatcher.replaceAll("(.)");

        Matcher multipleMacher = multiplePattern.matcher(wildcard);
        wildcard = multipleMacher.replaceAll(")|(");

        wildcard = "(" + wildcard + ")";

        // 2006.11.21 민병석
        if (ignoreCase) return Pattern.compile(wildcard);
        else return Pattern.compile(wildcard, Pattern.CASE_INSENSITIVE);
    }

}
