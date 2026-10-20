package com.ocp.communication_service.controller;
import com.ocp.communication_service.service.AttachmentService;
import com.ocp.communication_service.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
@RestController @RequestMapping("/api/conversations/{conversationId}") @RequiredArgsConstructor
public class AttachmentController {
 private final AttachmentService service;
 @PostMapping(value="/messages/{messageId}/attachments",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)@ResponseStatus(HttpStatus.CREATED)public AttachmentService.View upload(@PathVariable UUID conversationId,@PathVariable UUID messageId,@RequestParam("file")MultipartFile file,@AuthenticationPrincipal AuthenticatedUser user)throws java.io.IOException{return service.upload(conversationId,messageId,file,user==null?null:user.userId());}
 @GetMapping("/attachments/{id}")public ResponseEntity<Resource> download(@PathVariable UUID conversationId,@PathVariable UUID id,@AuthenticationPrincipal AuthenticatedUser user){var a=service.metadata(conversationId,id,user==null?null:user.userId());return ResponseEntity.ok().header("X-Content-Type-Options","nosniff").header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.attachment().filename(a.getOriginalName(),java.nio.charset.StandardCharsets.UTF_8).build().toString()).contentType(MediaType.parseMediaType(a.getContentType())).contentLength(a.getSize()).body(service.resource(a));}
}
