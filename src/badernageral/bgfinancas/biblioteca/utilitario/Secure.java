/*
Copyright 2012-2017 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

package badernageral.bgfinancas.biblioteca.utilitario;

import badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Secure {

    private Secure() { }

    public static String md5(String senha) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Janela.showException(ex);
        }
        md.update(senha.getBytes());
        byte[] xx = md.digest();
        String n2 = null;
        StringBuilder resposta = new StringBuilder();
        for (int i = 0; i < xx.length; i++) {//para todos os bytes...
            n2 = Integer.toHexString(0XFF & ((int) (xx[i])));
            if (n2.length() < 2) {
                n2 = "0" + n2;
            }
            resposta.append(n2);
        }
        return resposta.toString();
    }
    
}
