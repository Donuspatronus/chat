/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Dona
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/ChatServlet"})
public class ChatServlet extends HttpServlet {
    private HashMap<String, HttpServletResponse> clients=new HashMap<>();
    private HashMap<String, Long> listas = new HashMap<String, Long>();
    Calendar d=Calendar.getInstance();
    String eliminar="";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        response.setContentType("text/event-stream; charset=utf-8");
        
        clients.put(UUID.randomUUID().toString(),response);
        
        while(true){
            try{
                Thread.sleep(5000);
                Iterator<String> keySetIterator = listas.keySet().iterator(); 
                Long l;
                d.setTime(new Date());
                while(keySetIterator.hasNext()){ 
                    String key = keySetIterator.next(); 
                    l=listas.get(key);  
                    if(d.getTimeInMillis()-l>10000){
                        eliminar=key;
                        if(key!="inicial"){
                            listas.remove(key);
                        }
                        
                    }
                //    System.out.println("key: " + key + " value: " + listas.get(key)); 
                }

            }catch(Exception e){
                
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        Calendar d=Calendar.getInstance();
        d.setTime(new Date());              
        String text=request.getParameter("text");
        String user=request.getParameter("user");
        String emoticon=request.getParameter("emoticon");
        String nombreGrupo=request.getParameter("grupo");
        String sala=request.getParameter("sala");
        listas.put("inicial",d.getTimeInMillis());
       d.get(Calendar.MILLISECOND);
       //si se recibe gruponuevo o el grupo de donde viene
        if(nombreGrupo!="null"||sala!= "null"){
            //si hay grupo nuevo 
            if(nombreGrupo!="null"){
                //se guarda y se anota cuando se creo
                listas.put(nombreGrupo,d.getTimeInMillis());
            }else{
                //se actualiza el tiempo de creacion del grupo
                listas.put(sala,d.getTimeInMillis());
            }
            
        }       
        
        
        for(HttpServletResponse c: clients.values()){            
            //para borrar las salas
            c.getWriter().write("event:recogeSalas\n");
            c.getWriter().write("data:{\"otrocoso\":\""+eliminar+"\"}\n\n");
            c.getWriter().flush();
            
            //para agregar grupo
            c.getWriter().write("event:agregaGrupo\n");
            c.getWriter().write("data:{\"grupo\":\""+nombreGrupo+"\"}\n\n");
            c.getWriter().flush();
            
            //para agregar el mensaje, usuario y el grupo de donde viene
            c.getWriter().write("data:{\"type\":\"message\", \"message\":\""+text+"\",\"user\": \""+user+"\",\"sala\": \""+sala+"\",\"eliminar\": \""+eliminar+"\"}\n\n");
            c.getWriter().flush();
            
            //para enviar el emoticon
            /*c.getWriter().write("event:emoji\n");
            c.getWriter().write("data:{\"emoticon\":\""+emoticon+"\",\"user\":\""+user+"\"}\n\n");
            c.getWriter().flush();*/
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
