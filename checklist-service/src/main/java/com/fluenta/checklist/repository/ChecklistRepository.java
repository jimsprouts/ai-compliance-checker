package com.fluenta.checklist.repository;

import com.fluenta.checklist.model.Checklist;
import com.fluenta.checklist.model.ChecklistItem;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChecklistRepository {
    private final Map<String, Checklist> checklists = new HashMap<>();

    public ChecklistRepository() {
        initializeDefaultChecklists();
    }

    private void initializeDefaultChecklists() {
        Checklist iso27001 = Checklist.builder()
                .id("iso-27001-simplified")
                .name("ISO 27001 Essential Controls")
                .description("Simplified ISO 27001 compliance checklist")
                .items(Arrays.asList(
                        ChecklistItem.builder()
                                .id("AC-1")
                                .category("Access Control")
                                .requirement("Password policy documented and enforced")
                                .hints(Arrays.asList("password policy", "security guidelines", "authentication"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("AC-2")
                                .category("Access Control")
                                .requirement("User access reviews conducted quarterly")
                                .hints(Arrays.asList("access review", "user audit", "quarterly review"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("AC-3")
                                .category("Access Control")
                                .requirement("Administrative access logged and monitored")
                                .hints(Arrays.asList("admin access", "logging", "audit trail"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("IM-1")
                                .category("Incident Management")
                                .requirement("Incident response plan documented")
                                .hints(Arrays.asList("incident response", "response plan", "security incident"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("IM-2")
                                .category("Incident Management")
                                .requirement("Incident log maintained and reviewed")
                                .hints(Arrays.asList("incident log", "incident tracking", "security events"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("IM-3")
                                .category("Incident Management")
                                .requirement("Recovery procedures tested annually")
                                .hints(Arrays.asList("recovery", "disaster recovery", "business continuity"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("DP-1")
                                .category("Data Protection")
                                .requirement("Backup policy defined and implemented")
                                .hints(Arrays.asList("backup", "backup policy", "data backup"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("DP-2")
                                .category("Data Protection")
                                .requirement("Encryption standards documented")
                                .hints(Arrays.asList("encryption", "cryptography", "data protection"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("DP-3")
                                .category("Data Protection")
                                .requirement("Data retention policy exists and enforced")
                                .hints(Arrays.asList("retention", "data retention", "data lifecycle"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build(),
                        ChecklistItem.builder()
                                .id("RM-1")
                                .category("Risk Management")
                                .requirement("Risk assessment conducted annually")
                                .hints(Arrays.asList("risk assessment", "risk analysis", "threat assessment"))
                                .status(ChecklistItem.ItemStatus.PENDING)
                                .evidence(new ArrayList<>())
                                .build()
                ))
                .build();

        checklists.put(iso27001.getId(), iso27001);
    }

    public List<Checklist> findAll() {
        return new ArrayList<>(checklists.values());
    }

    public Optional<Checklist> findById(String id) {
        return Optional.ofNullable(checklists.get(id));
    }

    public Checklist save(Checklist checklist) {
        checklists.put(checklist.getId(), checklist);
        return checklist;
    }
}
