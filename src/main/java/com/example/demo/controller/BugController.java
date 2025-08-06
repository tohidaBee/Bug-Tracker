//
//
//package com.example.demo.controller;
//
//import com.example.demo.model.Bug;
//import com.example.demo.model.User;
//import com.example.demo.repository.BugRepository;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.security.JwtUtil;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@CrossOrigin(origins = "http://localhost:5173")
//@RestController
//@RequestMapping("/bugs")
//public class BugController {
//
//    private final BugRepository bugRepository;
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//
//    public BugController(BugRepository bugRepository, UserRepository userRepository, JwtUtil jwtUtil) {
//        this.bugRepository = bugRepository;
//        this.userRepository = userRepository;
//        this.jwtUtil = jwtUtil;
//    }
//
//    // Report a new bug (uses JWT to set reportedBy)
//    @PostMapping("/report")
//    public ResponseEntity<?> reportBug(@RequestBody Bug bug,
//                                       @RequestHeader("Authorization") String token) {
//        try {
//            // Extract email/username from JWT
//            String jwt = token.replace("Bearer ", "");
//            String email = jwtUtil.extractUsername(jwt); // use your JWT utility class
//
//            // Find the user from email
//            User reporter = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            bug.setReportedBy(reporter); // set reportedBy automatically
//
//            // If assignedTo exists, set status as ASSIGNED else OPEN
//            if (bug.getAssignedTo() != null && bug.getAssignedTo().getId() != null) {
//                bug.setAssignedTo(userRepository.findById(bug.getAssignedTo().getId())
//                        .orElseThrow(() -> new RuntimeException("Assigned user not found")));
//                bug.setStatus("ASSIGNED");
//            } else {
//                bug.setStatus("OPEN");
//            }
//
//            Bug saved = bugRepository.save(bug);
//            return ResponseEntity.ok(saved);
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//
//    // Assign a bug to user
//    @PutMapping("/{bugId}/assign/{userId}")
//    public ResponseEntity<?> assignBug(@PathVariable Long bugId, @PathVariable Long userId) {
//        try {
//            Bug bug = bugRepository.findById(bugId)
//                    .orElseThrow(() -> new RuntimeException("Bug not found"));
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            bug.setAssignedTo(user);
//            bug.setStatus("ASSIGNED");
//            return ResponseEntity.ok(bugRepository.save(bug));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    // Get all bugs
//    @GetMapping
//    public List<Bug> getAllBugs() {
//        return bugRepository.findAll();
//    }
//
//    // Update bug status
//    @PutMapping("/{bugId}/status/{status}")
//    public ResponseEntity<?> updateBugStatus(@PathVariable Long bugId, @PathVariable String status) {
//        return bugRepository.findById(bugId)
//                .map(bug -> {
//                    bug.setStatus(status.toUpperCase());
//                    return ResponseEntity.ok(bugRepository.save(bug));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // Close bug
//    @PatchMapping("/{bugId}/close")
//    public ResponseEntity<?> closeBug(@PathVariable Long bugId) {
//        return bugRepository.findById(bugId)
//                .map(bug -> {
//                    bug.setStatus("CLOSED");
//                    return ResponseEntity.ok(bugRepository.save(bug));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // Edit bug
//    @PutMapping("/{bugId}/edit")
//    public ResponseEntity<?> editBug(@PathVariable Long bugId, @RequestBody Bug updatedBug) {
//        return bugRepository.findById(bugId)
//                .map(bug -> {
//                    bug.setTitle(updatedBug.getTitle());
//                    bug.setDescription(updatedBug.getDescription());
//                    bug.setSeverity(updatedBug.getSeverity().toUpperCase());
//                    return ResponseEntity.ok(bugRepository.save(bug));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // Get bugs by status
//    @GetMapping("/status/{status}")
//    public List<Bug> getBugsByStatus(@PathVariable String status) {
//        return bugRepository.findAll().stream()
//                .filter(bug -> bug.getStatus().equalsIgnoreCase(status)).toList();
//    }
//
//    // Get bugs assigned to specific user
//    @GetMapping("/assigned/{userId}")
//    public List<Bug> getBugsAssignedToUser(@PathVariable Long userId) {
//        return bugRepository.findAll().stream()
//                .filter(bug -> bug.getAssignedTo() != null && bug.getAssignedTo().getId().equals(userId)).toList();
//    }
//}
//









package com.example.demo.controller;

import com.example.demo.model.Bug;
import com.example.demo.model.User;
import com.example.demo.repository.BugRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/bugs")
public class BugController {

    private final BugRepository bugRepository;
    private final UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public BugController(BugRepository bugRepository, UserRepository userRepository) {
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
    }

    // Report a new bug
    @PostMapping("/report")
    public ResponseEntity<?> reportBug(
            @RequestBody Bug bug,
            @RequestHeader("Authorization") String token) {
        try {
            // Extract email from token
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            // Find logged-in user
            User reporter = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            bug.setReportedBy(reporter);

            // Check assignedTo
            if (bug.getAssignedTo() != null && bug.getAssignedTo().getId() != null) {
                bug.setAssignedTo(userRepository.findById(bug.getAssignedTo().getId())
                        .orElseThrow(() -> new RuntimeException("Assigned user not found")));
                bug.setStatus("ASSIGNED");
            } else {
                bug.setStatus("OPEN");
            }

            Bug saved = bugRepository.save(bug);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Assign a bug to user
    @PutMapping("/{bugId}/assign/{userId}")
    public ResponseEntity<?> assignBug(@PathVariable Long bugId, @PathVariable Long userId) {
        try {
            Bug bug = bugRepository.findById(bugId)
                    .orElseThrow(() -> new RuntimeException("Bug not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            bug.setAssignedTo(user);
            bug.setStatus("ASSIGNED");
            return ResponseEntity.ok(bugRepository.save(bug));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all bugs
    @GetMapping
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    // Update bug status
    @PutMapping("/{bugId}/status/{status}")
    public ResponseEntity<?> updateBugStatus(@PathVariable Long bugId, @PathVariable String status) {
        return bugRepository.findById(bugId)
                .map(bug -> {
                    bug.setStatus(status.toUpperCase());
                    return ResponseEntity.ok(bugRepository.save(bug));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Close bug
    @PatchMapping("/{bugId}/close")
    public ResponseEntity<?> closeBug(@PathVariable Long bugId) {
        return bugRepository.findById(bugId)
                .map(bug -> {
                    bug.setStatus("CLOSED");
                    return ResponseEntity.ok(bugRepository.save(bug));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Edit bug
    @PutMapping("/{bugId}/edit")
    public ResponseEntity<?> editBug(@PathVariable Long bugId, @RequestBody Bug updatedBug) {
        return bugRepository.findById(bugId)
                .map(bug -> {
                    bug.setTitle(updatedBug.getTitle());
                    bug.setDescription(updatedBug.getDescription());
                    bug.setSeverity(updatedBug.getSeverity().toUpperCase());
                    return ResponseEntity.ok(bugRepository.save(bug));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get bugs by status
    @GetMapping("/status/{status}")
    public List<Bug> getBugsByStatus(@PathVariable String status) {
        return bugRepository.findAll().stream()
                .filter(bug -> bug.getStatus().equalsIgnoreCase(status)).toList();
    }

    // Get bugs assigned to specific user
    @GetMapping("/assigned/{userId}")
    public List<Bug> getBugsAssignedToUser(@PathVariable Long userId) {
        return bugRepository.findAll().stream()
                .filter(bug -> bug.getAssignedTo() != null && bug.getAssignedTo().getId().equals(userId)).toList();
    }
}
