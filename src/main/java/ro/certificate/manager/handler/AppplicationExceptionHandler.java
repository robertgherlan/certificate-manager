package ro.certificate.manager.handler;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.certificate.manager.exceptions.InternalServerError;
import ro.certificate.manager.exceptions.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AppplicationExceptionHandler {

    private static final Logger logger = Logger.getLogger(AppplicationExceptionHandler.class);

    @ExceptionHandler(value = {InternalServerError.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String customExceptions(InternalServerError ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Cannot process your request: " + ex.getMessage());
        return "redirect:/500";
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String userNotFoundExceptions(UserNotFoundException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Cannot process your request: " + ex.getMessage());
        return "redirect:/500";
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public String methodNotAllowed(HttpRequestMethodNotSupportedException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("Cannot process your request: ", ex.getMessage());
        return "redirect:/503";
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public String noHandlerDefined(NoHandlerFoundException ex) {
        logger.error(ex.getMessage());
        return "redirect:/404";
    }

    @ExceptionHandler(value = {MultipartException.class})
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public String multipart(MultipartException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("Cannot process your request: ", ex.getMessage());
        return "redirect:/503";
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public String uploadSizeExceed(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("Cannot process your request: ", ex.getMessage());
        return "redirect:/503";
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    public String debug(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("Cannot process your request: ", ex.getMessage());
        return "redirect:/503";
    }
}
