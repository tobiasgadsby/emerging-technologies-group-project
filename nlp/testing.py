from transcription import TranscriptionService
import os


def test_transcription(audio_path):
    #initialize the service (use 'tiny' for a fast test, or 'base' for a better model)
    service = TranscriptionService(model_size="base")
    
    if not os.path.exists(audio_path):
        print(f"Error: Please put a file named '{audio_path}' in this folder.")
        return

    #run transcription
    print("Starting Transcription \n")
    result = service.transcribe(audio_path)
    
    print(f"Result: \n{result}\n")
    
    if len(result) > 0:
        print("Success: Transcription generated some text.")
    else:
        print("Failed: Transcription returned no text.")

if __name__ == "__main__":
    audio_path = "C:\\Users\\aaron\\OneDrive - Cardiff University\\Computer Science\\Year 3\\CM3202_Emerging_Technology\\emerging-technologies-group-project\\nlp_pipeline\\Recording.mp3" 
    test_transcription(audio_path)