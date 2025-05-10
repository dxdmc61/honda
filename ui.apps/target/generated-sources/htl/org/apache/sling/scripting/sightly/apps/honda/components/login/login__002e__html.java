/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.sling.scripting.sightly.apps.honda.components.login;

import java.io.PrintWriter;
import java.util.Collection;
import javax.script.Bindings;

import org.apache.sling.scripting.sightly.render.RenderUnit;
import org.apache.sling.scripting.sightly.render.RenderContext;

public final class login__002e__html extends RenderUnit {

    @Override
    protected final void render(PrintWriter out,
                                Bindings bindings,
                                Bindings arguments,
                                RenderContext renderContext) {
// Main Template Body -----------------------------------------------------------------------------

Object _dynamic_currentnode = bindings.get("currentnode");
Object _dynamic_properties = bindings.get("properties");
out.write("<!-- /apps/honda/components/login/login.html -->\n<cq:includeClientLib categories=\"honda.login\"/>\n<div class=\"login-page\"");
{
    Object var_attrvalue0 = renderContext.getObjectModel().resolveProperty(_dynamic_currentnode, "path");
    {
        Object var_attrcontent1 = renderContext.call("xss", var_attrvalue0, "attribute");
        {
            boolean var_shoulddisplayattr3 = (((null != var_attrcontent1) && (!"".equals(var_attrcontent1))) && ((!"".equals(var_attrvalue0)) && (!((Object)false).equals(var_attrvalue0))));
            if (var_shoulddisplayattr3) {
                out.write(" data-cq-path");
                {
                    boolean var_istrueattr2 = (var_attrvalue0.equals(true));
                    if (!var_istrueattr2) {
                        out.write("=\"");
                        out.write(renderContext.getObjectModel().toString(var_attrcontent1));
                        out.write("\"");
                    }
                }
            }
        }
    }
}
out.write(">\n    <img");
{
    Object var_attrvalue4 = renderContext.call("xss", renderContext.getObjectModel().resolveProperty(_dynamic_properties, "hondaLogo"), "uri");
    {
        boolean var_shoulddisplayattr7 = ((!"".equals(var_attrvalue4)) && (!((Object)false).equals(var_attrvalue4)));
        if (var_shoulddisplayattr7) {
            out.write(" src");
            {
                boolean var_istrueattr6 = (var_attrvalue4.equals(true));
                if (!var_istrueattr6) {
                    out.write("=\"");
                    out.write(renderContext.getObjectModel().toString(var_attrvalue4));
                    out.write("\"");
                }
            }
        }
    }
}
out.write(" alt=\"Honda Logo\" class=\"honda-logo\"/>\n    <header class=\"login-header\">\n        <h2>");
{
    Object var_8 = renderContext.call("xss", renderContext.getObjectModel().resolveProperty(_dynamic_properties, "headingText"), "text");
    out.write(renderContext.getObjectModel().toString(var_8));
}
out.write("</h2>\n    </header>\n\n    <div class=\"login-content\">\n        <div class=\"login-form-container\">\n            <div class=\"login-container\">\n                <h2>Login</h2>\n                <form method=\"POST\" action=\"https://your-appscript-url\" class=\"login-form\">\n                    <input type=\"text\" name=\"dealerNumber\" placeholder=\"Dealer Number\" required/>\n                    <input type=\"text\" name=\"userId\" placeholder=\"User ID\" required/>\n                    <input type=\"password\" name=\"password\" placeholder=\"Password\" required/>\n                   <!-- <div class=\"remember-forgot\">\n                        <label>\n                            <input type=\"checkbox\" name=\"rememberMe\" />\n                            Remember Me\n                        </label>\n                        <a href=\"/forgot-password\">Forgot Password?</a>\n                    </div>-->\n                    <button type=\"submit\">Login</button>\n                </form>\n            </div>\n        </div>\n        <div class=\"login-side-image\">\n            <img");
{
    Object var_attrvalue9 = renderContext.call("xss", renderContext.getObjectModel().resolveProperty(_dynamic_properties, "sideImage"), "uri");
    {
        boolean var_shoulddisplayattr12 = ((!"".equals(var_attrvalue9)) && (!((Object)false).equals(var_attrvalue9)));
        if (var_shoulddisplayattr12) {
            out.write(" src");
            {
                boolean var_istrueattr11 = (var_attrvalue9.equals(true));
                if (!var_istrueattr11) {
                    out.write("=\"");
                    out.write(renderContext.getObjectModel().toString(var_attrvalue9));
                    out.write("\"");
                }
            }
        }
    }
}
out.write(" alt=\"Login Visual\"/>\n        </div>\n    </div>\n\n    <footer class=\"login-footer\">\n        <p>&copy; 2025 American Honda Motor Co., Inc. All information contained herein applies to U.S. products only.\n        </p>\n    </footer>\n</div>\n<style>\n    .login-page {\n        font-family: Arial, sans-serif;\n        background-color: #f7f7f7;\n        padding: 2rem;\n    }\n\n    .honda-logo {\n        width: 150px;\n        margin-bottom: 1rem;\n    }\n\n    .login-header h2 {\n        color: #cc0000;\n        text-align: center;\n    }\n\n    .login-content {\n        display: flex;\n        justify-content: space-between;\n        margin-top: 2rem;\n    }\n\n    .login-form-container {\n        flex: 1;\n        padding: 1rem;\n    }\n\n    .login-container {\n        max-width: 400px;\n        margin: 0 auto;\n        background: #fff;\n        padding: 2rem;\n        border-radius: 8px;\n        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n    }\n\n    .login-form input {\n        width: 100%;\n        padding: 0.8rem;\n        margin-bottom: 1rem;\n        border: 1px solid #ccc;\n        border-radius: 4px;\n    }\n\n    .login-form button {\n        width: 100%;\n        padding: 0.8rem;\n        background-color: #cc0000;\n        color: white;\n        border: none;\n        border-radius: 4px;\n        font-weight: bold;\n    }\n\n    .login-side-image img {\n        max-width: 100%;\n        height: auto;\n    }\n\n    .login-footer {\n        margin-top: 2rem;\n        text-align: center;\n        font-size: 0.9rem;\n        color: #555;\n    }\n</style>");


// End Of Main Template Body ----------------------------------------------------------------------
    }



    {
//Sub-Templates Initialization --------------------------------------------------------------------



//End of Sub-Templates Initialization -------------------------------------------------------------
    }

}

