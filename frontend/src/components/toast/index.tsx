import type { ReactNode } from "react";

interface ToastProps {
  children: ReactNode;
  isError?: boolean;
  confirm?: {
    message?: string; // optional text before buttons
    onConfirm: () => void;
    onCancel?: () => void;
  };
}

const Toast = ({ children, isError = false, confirm }: ToastProps) => {
  return (
    <div
      className={`${
        isError ? "bg-red-200" : "bg-green-200"
      } fixed top-30 left-1/2 -translate-x-1/2 shadow border-2 border-black px-4 py-2 rounded ransition-opacity z-50`}
    >
      <div className="mb-2">{children}</div>

      {confirm && (
        <div className="flex flex-col items-center gap-2 justify-center">
          {confirm.message && (
            <span className="text-sm text-gray-700">{confirm.message}</span>
          )}
          <div className="flex flex-row gap-2">
            <button
              onClick={confirm.onCancel}
              className="bg-gray-200 px-3 py-1 rounded cursor-pointer border-2 border-black hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              onClick={confirm.onConfirm}
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
