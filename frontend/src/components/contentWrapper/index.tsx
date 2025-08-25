import type { ReactNode } from "react";

interface ContentWrapperProps {
  children: ReactNode;
}
const ContentWrapper = ({ children }: ContentWrapperProps) => {
  return (
    <div className="flex justify-center">
      <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded m-4">
        {children}
      </div>
    </div>
  );
};

export default ContentWrapper;
