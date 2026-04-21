import whisper
import os

class TranscriptionService:
    def __init__(self, model_size="base"):
        # Uses CPU by default, since no CUDA available on deployment server
        self.device = "cpu"
        
        # Loads model once into memory to avoid lag
        self.model = whisper.load_model(model_size, device=self.device)

    def transcribe(self, audio_path):
        # Checks if the file can be found
        if not os.path.exists(audio_path):
            return "Error: Audio file not found at path."
        
        # Gets the transcription of the audio
        result = self.model.transcribe(audio_path, fp16=(self.device == "cuda"))
        return result["text"].strip()

#
if __name__ == "__main__":
    try:
        service = TranscriptionService(model_size="tiny")
        print("Whisper and Torch are correctly installed and loaded")
    except Exception as e:
        print(f"Setup failed: {e}")