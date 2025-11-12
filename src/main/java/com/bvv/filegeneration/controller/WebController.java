package com.bvv.filegeneration.controller;

import com.bvv.filegeneration.dto.GenerateFileRequestDTO;
import com.bvv.filegeneration.dto.GenerationJobDTO;
import com.bvv.filegeneration.dto.TemplateDTO;
import com.bvv.filegeneration.service.GenerationJobService;
import com.bvv.filegeneration.service.GenerationLogService;
import com.bvv.filegeneration.service.TemplateService;
import com.bvv.filegeneration.service.TemplateImportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

    private final TemplateService templateService;
    private final GenerationJobService jobService;
    private final GenerationLogService logService;
    private final TemplateImportService templateImportService;

    public WebController(TemplateService templateService,
                        GenerationJobService jobService,
                        GenerationLogService logService,
                        TemplateImportService templateImportService) {
        this.templateService = templateService;
        this.jobService = jobService;
        this.logService = logService;
        this.templateImportService = templateImportService;
    }

    /**
     * Page d'accueil
     */
    @GetMapping
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Accueil - File Generator");
        model.addAttribute("currentUri", request.getRequestURI());
        return "index";
    }

    /**
     * Page d'import de fichier
     */
    @GetMapping("/import")
    public String importPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Import Fichier");
        model.addAttribute("currentUri", request.getRequestURI());
        return "import-template";
    }

    /**
     * Traitement de l'upload
     */
    @PostMapping("/import")
    public String handleImport(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "prefix", required = false) String prefix,
                              RedirectAttributes redirectAttributes) {
        try {
            // Valider le fichier
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Veuillez sélectionner un fichier à importer");
                return "redirect:/import";
            }

            // Importer le fichier et créer le template
            TemplateDTO templateDTO = templateImportService.importFile(file, prefix);

            redirectAttributes.addFlashAttribute("successMessage",
                "Fichier importé avec succès ! Template '" + templateDTO.getName() + "' créé avec " +
                (templateDTO.getFields() != null ? templateDTO.getFields().size() : 0) + " champs.");

            return "redirect:/templates/" + templateDTO.getId();

        } catch (Exception e) {
            e.printStackTrace(); // Pour le debug
            redirectAttributes.addFlashAttribute("errorMessage",
                "Erreur lors de l'import: " + e.getMessage());
            return "redirect:/import";
        }
    }

    /**
     * Liste des gabarits
     */
    @GetMapping("/templates")
    public String listTemplates(Model model, HttpServletRequest request) {
        List<TemplateDTO> templates = templateService.getAllTemplates();
        model.addAttribute("templates", templates);
        model.addAttribute("pageTitle", "Liste des Gabarits");
        model.addAttribute("currentUri", request.getRequestURI());
        return "templates-list";
    }

    /**
     * Détail d'un gabarit
     */
    @GetMapping("/templates/{id}")
    public String templateDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        TemplateDTO template = templateService.getTemplateById(id);
        model.addAttribute("template", template);
        model.addAttribute("pageTitle", "Détail Gabarit - " + template.getName());
        model.addAttribute("currentUri", request.getRequestURI());
        return "template-detail";
    }

    /**
     * Supprimer un gabarit
     */
    @PostMapping("/templates/{id}/delete")
    public String deleteTemplate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            templateService.deleteTemplate(id);
            redirectAttributes.addFlashAttribute("successMessage",
                "Gabarit supprimé avec succès");
            return "redirect:/templates";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Erreur lors de la suppression: " + e.getMessage());
            return "redirect:/templates/" + id;
        }
    }

    /**
     * Page de génération de fichier
     */
    @GetMapping("/generate")
    public String generatePage(Model model, HttpServletRequest request) {
        List<TemplateDTO> templates = templateService.getAllTemplates();
        model.addAttribute("templates", templates);
        model.addAttribute("request", new GenerateFileRequestDTO());
        model.addAttribute("pageTitle", "Générer Fichier");
        model.addAttribute("currentUri", request.getRequestURI());
        return "generate-file";
    }

    /**
     * Traitement de la génération
     */
    @PostMapping("/generate")
    public String handleGenerate(@ModelAttribute GenerateFileRequestDTO request,
                                RedirectAttributes redirectAttributes) {
        try {
            GenerationJobDTO job = jobService.createJob(
                request.getTemplateId(),
                request.getTotalLines(),
                request.getErrorLines(),
                request.getOutputFormat()
            );
            redirectAttributes.addFlashAttribute("successMessage",
                "Tâche de génération créée avec succès (ID: " + job.getId() + ")");
            return "redirect:/jobs/" + job.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Erreur lors de la génération: " + e.getMessage());
            return "redirect:/generate";
        }
    }

    /**
     * Liste des tâches
     */
    @GetMapping("/jobs")
    public String listJobs(Model model, HttpServletRequest request) {
        List<GenerationJobDTO> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        model.addAttribute("pageTitle", "Liste des Tâches");
        model.addAttribute("currentUri", request.getRequestURI());
        return "jobs-list";
    }

    /**
     * Détail d'une tâche
     */
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        GenerationJobDTO job = jobService.getJobById(id);
        model.addAttribute("job", job);
        model.addAttribute("pageTitle", "Détail Tâche #" + id);
        model.addAttribute("currentUri", request.getRequestURI());
        return "job-detail";
    }

    /**
     * Historique des logs
     */
    @GetMapping("/logs")
    public String logsHistory(Model model, HttpServletRequest request) {
        model.addAttribute("logs", logService.getAllLogs());
        model.addAttribute("pageTitle", "Historique des Exécutions");
        model.addAttribute("currentUri", request.getRequestURI());
        return "logs-history";
    }

    /**
     * Page de tests automatisés
     */
    @GetMapping("/auto-test")
    public String autoTest(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Tests Automatisés");
        model.addAttribute("currentUri", request.getRequestURI());
        return "auto-test";
    }

    /**
     * Page de gestion des URLs cibles
     */
    @GetMapping("/target-urls")
    public String targetUrls(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "URLs de Destination");
        model.addAttribute("currentUri", request.getRequestURI());
        return "target-urls";
    }
}

