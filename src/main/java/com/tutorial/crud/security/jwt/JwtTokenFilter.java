package com.tutorial.crud.security.jwt;

import com.tutorial.crud.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtTokenFilter extends OncePerRequestFilter {
    //Se ejecuta por cada peticion
    private final static Logger logger= LoggerFactory.getLogger(JwtProvider.class);

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(req);
            if(token !=null && jwtProvider.validateToken(token)){
                String nombreUsuario = jwtProvider.getNombreUsuarioFromToken(token);
                UserDetails userDetails= userDetailsService.loadUserByUsername(nombreUsuario);
                UsernamePasswordAuthenticationToken auth=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }catch (Exception e){
            logger.error("Fail en el método doFilter");
        }
        filterChain.doFilter(req,res);
    }

    private String getToken(HttpServletRequest request){
        String header=request.getHeader("Authorization");
        if(header !=null && header.startsWith("Bearer"))
            return header.replace("Bearer ","");
        return null;
    }
}
