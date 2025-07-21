package com.example.kotlin_spring_project.controller

import com.example.kotlin_spring_project.dto.ApiResponse
import com.example.kotlin_spring_project.dto.AuthRequest
import com.example.kotlin_spring_project.dto.AuthResponse
import com.example.kotlin_spring_project.model.User
import com.example.kotlin_spring_project.security.JwtTokenUtil
import com.example.kotlin_spring_project.service.UserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*

@RestController
class HelloController(
    @Qualifier("customAuthenticationManager")
    private val customAuthenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val userService: UserService
) {

    @PostMapping("/auth/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val auth: Authentication = customAuthenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            val token = jwtTokenUtil.generateToken(auth.name)
            ResponseEntity.ok(ApiResponse.success(AuthResponse(token)))
        } catch (e: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.failure("Invalid username or password"))
        }
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        val users = userService.findAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/hello")
    fun helloWorld(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, world!")
    }
}
