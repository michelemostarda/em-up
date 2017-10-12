/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.mashup.interpreter;

import junit.framework.TestCase;
import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

import java.util.List;

/**
 * This class contains the syntetic scenario used to
 * validate the MU language main features.
 */
public class SynteticValidationScenarioTest extends TestCase {

    /**
     * Interpreter used as target test.
     */
    private Interpreter interpreter;

    protected void setUp() throws Exception {
        interpreter = new Interpreter();
    }

    protected void tearDown() throws Exception {
        interpreter = null;
    }

    /**
     * Tests complex scenario.
     *
     * <h2>Scenario formalization</h2>
     *
     * <pre>
     *
     * We've a Web service S1 returning a list of restaurant names (RNi) in a specified city,
     * providing for each restaurant the street (RSi) in which it is located.
     *
     * We've also a Web service S2 returning a list of hotel names (HNi) near a given street
     * with relative number of stars (SRi).
     *
     * We want to combine these sources to obtain a list of restaurants in a given city
     * with a sublist of hotels with relative stars near each restaurant.
     *
     * </pre>
     *
     * @throws InterpreterException
     */
    public void testSyntheticValidationScenario() throws InterpreterException {
        final String in =
            "#01# S1(city) : TGet('http://findrestaurants.com/find?city=_', city);\n" +
            "\n" +
            "#02# RawRomeRestaurants() : S1('Rome');\n" +
            "\n" +
            "#03# ListOfRomeRestaurants() : XPath( 'html/body/table/tr', RawRomeRestaurants() );\n" +
            "\n" +
            "#04# RestaurantName(row)   : XPath( 'td[0]' , row );\n" +
            "\n" +
            "#05# RestaurantStreet(row) : XPath( 'td[1]' , row );\n" +
            "\n" +
            "#06# S2(city,street) : TGet('http://findhotels.com/find?city=_street=_', List(city, street) );\n" +
            "\n" +
            "#07# HotelsInRomeAtStreet(street) : S2('Rome', street);\n" +
            "\n" +
            "#08# ListOfStreetHotels(street) : XPath( 'html/body/table/tr' , HotelsInRomeAtStreet(street) );\n" +
            "\n" +
            "#09# HotelName(row) : XPath( 'td[0]', row );\n" +
            "\n" +
            "#10# HotelStars(row) : XPath( 'td[1]' , row );\n" +
            "\n" +
            "#11# JsonHotel(row) : Jsonize( '{ \"hotel-name\" : %s, \"hotel-stars\" : %s }', List( HotelName(row), HotelStars(row) ) );\n" +
            " \n" +
            "#12# HotelsInStreet(street) : for( ListOfStreetHotels(street), hotel, JsonHotel(hotel) );\n" +
            "\n" +
            "#13# Restaurant(row) : Jsonize( '{ \"restaurant-name\" : %s, \"restaurant-street\" : %s, \"hotels\" : %a }', List(RestaurantName(row), street=RestaurantStreet(row), HotelsInStreet(street)) );\n" +
            "\n" +
            "#14# RomeRestaurants() : for( ListOfRomeRestaurants(), row,  Restaurant(row) );";

        interpreter.getProcessor().addPredicate("TGet", new FakeGetOperation() );

        interpreter.process(in);
        Value result = interpreter.process("RomeRestaurants();");
        String resultStr = result.asString().getNativeValue();
        System.out.println( "result: " + resultStr );
        assertEquals(
            "Unsespected result.",
                "[" +
                    "{" +
                        "\"restaurant-name\":\"RN1Rome\",\"restaurant-street\":\"RS1Rome\"," +
                        "\"hotels\":" +
                        "[" +
                        "{\"hotel-name\":\"HN1RomeRS1Rome\",\"hotel-stars\":\"SR1RomeRS1Rome\"}," +
                        "{\"hotel-name\":\"HN2RomeRS1Rome\",\"hotel-stars\":\"SR2RomeRS1Rome\"}," +
                        "{\"hotel-name\":\"HN3RomeRS1Rome\",\"hotel-stars\":\"SR3RomeRS1Rome\"}" +
                        "]" +
                    "}," +
                    "{" +
                        "\"restaurant-name\":\"RN2Rome\"," +
                        "\"restaurant-street\":\"RS2Rome\"," +
                        "\"hotels\":" +
                        "[" +
                        "{\"hotel-name\":\"HN1RomeRS2Rome\",\"hotel-stars\":\"SR1RomeRS2Rome\"}," +
                        "{\"hotel-name\":\"HN2RomeRS2Rome\",\"hotel-stars\":\"SR2RomeRS2Rome\"}," +
                        "{\"hotel-name\":\"HN3RomeRS2Rome\",\"hotel-stars\":\"SR3RomeRS2Rome\"}" +
                        "]" +
                    "}," +
                   "{" +
                        "\"restaurant-name\":\"RN3Rome\"," +
                        "\"restaurant-street\":\"RS3Rome\"," +
                        "\"hotels\":" +
                        "[" +
                        "{\"hotel-name\":\"HN1RomeRS3Rome\",\"hotel-stars\":\"SR1RomeRS3Rome\"}," +
                        "{\"hotel-name\":\"HN2RomeRS3Rome\",\"hotel-stars\":\"SR2RomeRS3Rome\"}," +
                        "{\"hotel-name\":\"HN3RomeRS3Rome\",\"hotel-stars\":\"SR3RomeRS3Rome\"}"  +
                        "]" +
                   "}" +
                "]",
                resultStr
        );

    }

    /**
     * Fake Get operation implementation used in #testMainScenario() test.
     */
    class FakeGetOperation extends NativeInvocable {

        public Signature getSignature() {
            return new Signature( new String[] {"URL", "PARAMS"} );
        }

        public String getShortDescription() {
            return "Test Get.";
        }

        public String getDescription() {
            return getShortDescription();
        }

        public Value execute(ExecutionContext context, ExecutionStack stack)
        throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
            String        url  = context.getValue("URL").asString().getNativeValue();
            List<Value> params = context.getValue("PARAMS").asList().getNativeValue();
            StringBuilder paramsString = new StringBuilder();
            for(Value p :  params) {
                paramsString.append( p.asString().getNativeValue() );
            }

            String ps = paramsString.toString();

            if( url.contains("findrestaurants") ) {
                return new StringValue(
                        htmlfy(
                            String.format(
                                "<table>" + // RNi : Restaurant i-th Name, RSi : Restaurant i-th Street
                                "<tr><td>RN1%s</td><td>RS1%s</td></tr>" +
                                "<tr><td>RN2%s</td><td>RS2%s</td></tr>" +
                                "<tr><td>RN3%s</td><td>RS3%s</td></tr>" +
                                "</table>",
                                ps, ps , ps, ps, ps, ps
                            )
                        )
                );
            }

            if( url.contains("findhotels") ) {
                return new StringValue(
                        htmlfy(
                            String.format(
                                "<table>" + // HNi : Hotel i-th name , SRi : Hotel i-th stars
                                "<tr><td>HN1%s</td><td>SR1%s</td></tr>" +
                                "<tr><td>HN2%s</td><td>SR2%s</td></tr>" +
                                "<tr><td>HN3%s</td><td>SR3%s</td></tr>" +
                                "</table>",
                                ps, ps, ps, ps, ps, ps
                            )
                        )
                );
            }

            throw new RuntimeException("Unsupported test request");
        }

        public Operation[] getInnerOperations() {
            return new Operation[]{this};
        }

        public String htmlfy(String s) {
            return "<html><body>" + s + "</body></html>";
        }
    }
}
