import { useEffect } from "react";

export type ToastConfig = {
  message: string;
  isError?: boolean;
  duration?: number;
  confirm?: {
    message?: string;
    onConfirm: () => void;
    onCancel?: () => void;
  };
} | null;

interface ToastProps {
  message: string;
  isError?: boolean;
  duration?: number;
  confirm?: {
    message?: string;
    onConfirm: () => void;
    onCancel?: () => void;
  };
  onClose: () => void;
}

const Toast = ({
  message,
  isError = false,
  duration = 2000,
  confirm,
  onClose,
}: ToastProps) => {
  useEffect(() => {
    if (!confirm) {
      const timer = setTimeout(() => {
        onClose();
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [confirm, duration, onClose]);

  return (
    <div
      className={`${
        isError ? "bg-red-200" : "bg-green-200"
      } fixed top-10 left-1/2 -translate-x-1/2 shadow border-2 border-black px-4 py-2 rounded transition-opacity z-50`}
    >
      <div className="mb-2">{message}</div>

      {confirm && (
        <div className="flex flex-col items-center gap-2 justify-center">
          {confirm.message && (
            <span className="text-sm text-gray-700">{confirm.message}</span>
          )}
          <div className="flex flex-row gap-2">
            <button
              onClick={() => {
                confirm.onCancel?.();
                onClose();
              }}
              className="bg-gray-200 px-3 py-1 rounded cursor-pointer border-2 border-black hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              onClick={() => {
                confirm.onConfirm();
                onClose();
              }}
              className="bg-yellow-200 text-black px-3 py-1 cursor-pointer rounded border-2 border-black hover:bg-yellow-300"
            >
              Yes
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Toast;
