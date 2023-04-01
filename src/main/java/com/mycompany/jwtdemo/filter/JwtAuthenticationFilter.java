package com.mycompany.jwtdemo.filter;


import com.mycompany.jwtdemo.service.CustomUserDetailService;
import com.mycompany.jwtdemo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//call this filter once by request
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //get the JWT Token from header and we will validate the token
        String userName=null;
        String token=null;
        String bearerToken=httpServletRequest.getHeader("Authorization");
        //check if token exist bearer text or not
        if (bearerToken !=null && bearerToken.startsWith("Bearer"))
        {
//            extract jwt token from bearerToken
            token=bearerToken.substring(7);
            try
            {
                //extract the username form the Bearer token
                userName=jwtUtil.extractUsername(token);

//                get user details
              UserDetails userDetails= customUserDetailService.loadUserByUsername(userName);

//              security checks
                if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null)
                {
                    UsernamePasswordAuthenticationToken upat=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(upat);

                }
                else {
                    System.out.println("Invalid Token !");
                }


            }
            catch (Exception ex)
            {
                ex.printStackTrace();

            }

        }
        else
        {
            System.out.println("Invalid Bearer Token Format !");
        }
//        if every thing is well then forward the filter to endpoint
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
